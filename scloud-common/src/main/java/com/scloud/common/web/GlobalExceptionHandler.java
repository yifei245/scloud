package com.scloud.common.web;

import com.scloud.common.core.BizException;
import com.scloud.common.core.ErrorCode;
import com.scloud.common.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    public Result<Void> biz(BizException ex) {
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> valid(MethodArgumentNotValidException ex) {
        return Result.fail(400, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> error(Exception ex) {
        log.error("系统异常", ex);
        return Result.fail(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage());
    }
}
