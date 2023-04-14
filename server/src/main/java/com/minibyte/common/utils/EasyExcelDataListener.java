package com.minibyte.common.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author dengwei
 * @ClassName EasyExcelDataListener.java
 * @Description excel导入
 * @Date 2020/04/02 18:32
 */
@Slf4j
@Data
@NoArgsConstructor
public class EasyExcelDataListener<T> extends AnalysisEventListener<T> {
    private List<T> dataList = new ArrayList<>();
    private Integer branch;
    private Consumer<List<T>> consumer;

    public EasyExcelDataListener(Consumer<List<T>> consumer, Integer branch) {
        this.consumer = consumer;
        this.branch = branch;
    }

    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        dataList.add(data);
        if (dataList.size() >= branch) {
            consumer.accept(dataList);
            // 存储完成清理 list
            dataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!CollectionUtils.isEmpty(dataList)) {
            consumer.accept(dataList);
        }
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);
    }
}
