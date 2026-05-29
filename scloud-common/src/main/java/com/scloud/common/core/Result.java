package com.scloud.common.core;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        result.setTraceId(TraceIdHolder.getTraceId());
        return result;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTraceId(TraceIdHolder.getTraceId());
        return result;
    }
}
