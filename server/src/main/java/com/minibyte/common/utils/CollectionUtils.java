package com.minibyte.common.utils;

import cn.hutool.core.collection.CollUtil;

import java.util.List;
import java.util.function.Consumer;

public final class CollectionUtils {
    /**
     * 列表批量子集消费
     *
     * @param list 列表
     * @param batchSizePer 每次消费数量
     * @param listConsumer 消费者
     * @param <T> List项类型
     */
    public static <T> void listBatchConsume(final List<T> list, final Integer batchSizePer, final Consumer<List<T>> listConsumer) {
        if (CollUtil.isEmpty(list) || null == listConsumer) {
            return;
        }
        int cursorStart = 0;
        int cursorEnd = 0;
        final int listSize = list.size();

        while (cursorStart < listSize) {
            cursorStart = cursorEnd;
            int tmpCursorEnd = cursorStart + batchSizePer;
            cursorEnd = Math.min(tmpCursorEnd, listSize);

            if (cursorStart < listSize) {
                List<T> subList = list.subList(cursorStart, cursorEnd);
                listConsumer.accept(subList);
            }
        }
    }
}
