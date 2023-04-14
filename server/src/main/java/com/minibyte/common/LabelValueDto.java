package com.minibyte.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: MiniByte
 * @date: 2021/11/10
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelValueDto {
    private String label;
    private String value;
}
