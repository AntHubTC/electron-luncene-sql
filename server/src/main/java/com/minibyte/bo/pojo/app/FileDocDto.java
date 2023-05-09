package com.minibyte.bo.pojo.app;

import lombok.Data;

@Data
public class FileDocDto {
    private String fileName;
    private String sqlName;
    private String descr;
    private String content;
}
