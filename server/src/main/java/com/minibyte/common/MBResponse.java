package com.minibyte.common;


import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class MBResponse<T> {
    // 业务状态码
    private String status;
    // 数据
    private T data;
    // 总记录数
    private long total;

    public MBResponse() {
    }

    public MBResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public MBResponse(String status, T data, long total) {
        this.status = status;
        this.data = data;
        this.total = total;
    }

    public static <T> MBResponse<T> ofSuccess(T data) {
        return new MBResponse<T>(MBConstant.RESPONSE_SUCCESS_STATUS, data);
    }

    public static MBResponse ofSuccessPage(IPage page) {
        return new MBResponse(MBConstant.RESPONSE_SUCCESS_STATUS, page.getRecords(), page.getTotal());
    }

    public static <T> MBResponse<T> ofFailure(T data) {
        return new MBResponse<T>(MBConstant.RESPONSE_FAIL_STATUS, data);
    }
}
