package com.minibyte.common.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Lambada工具
 */
public class LambadaTools {
    /**
     * 利用BiConsumer实现foreach循环支持index
     *
     * @param biConsumer
     * @param <T>
     * @return
     */
    public static <T> Consumer<T> forEachWithIndex(BiConsumer<T, Integer> biConsumer) {
        class IncrementInt{
            int i = 0;
            public int getAndIncrement(){
                return i++;
            }
        }
        IncrementInt incrementInt = new IncrementInt();
        return t -> biConsumer.accept(t, incrementInt.getAndIncrement());
    }
}
