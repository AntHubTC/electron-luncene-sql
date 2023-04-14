package com.minibyte.common.exception;

import com.minibyte.common.MBConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *  系统公共业务异常
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MBBizException extends RuntimeException{
    private String errorCode;
    private String errorMsg;

    public MBBizException() {
    }

    public MBBizException(String errorMsg) {
        super(errorMsg);
        this.errorCode = MBConstant.RESPONSE_FAIL_STATUS;
        this.errorMsg = errorMsg;
    }

    public MBBizException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
