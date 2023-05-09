package com.minibyte.bo.pojo.app;

import lombok.Data;

@Data
public class Condition {
    /**
     * 条件类型： 1按sql名称 2按文件名  3按作者  4按内容
     */
    private Integer type;
    /**
     * 条件值
     */
    private String value;
}
