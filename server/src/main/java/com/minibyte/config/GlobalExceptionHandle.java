package com.minibyte.config;

import cn.hutool.json.JSONUtil;
import com.minibyte.common.MBResponse;
import com.minibyte.common.exception.MBBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: MiniByte
 * @date: 2022/1/4
 * @description: 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(value = MBBizException.class)
    public MBResponse handleException(MBBizException e, HttpServletRequest request) {
        log.info("handleException params: {}", JSONUtil.toJsonStr(e));
        return new MBResponse(e.getErrorCode(), e.getErrorMsg());
    }
}
