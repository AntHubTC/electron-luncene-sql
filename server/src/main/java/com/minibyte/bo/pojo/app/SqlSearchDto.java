package com.minibyte.bo.pojo.app;

import lombok.Data;

import java.util.List;

/**
 * sql搜索DTO
 */
@Data
public class SqlSearchDto {
    private List<Condition> conditions;
}
