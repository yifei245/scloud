package com.scloud.common.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(401, "未认证或认证已过期"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源状态冲突"),
    BAD_REQUEST(400, "请求参数错误"),
    INTERNAL_ERROR(500, "系统异常");

    private final Integer code;
    private final String message;
}
