package com.scloud.common.core;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

public final class TraceIdHolder {
    public static final String TRACE_ID = "traceId";

    private TraceIdHolder() {
    }

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = IdUtil.fastSimpleUUID();
            MDC.put(TRACE_ID, traceId);
        }
        return traceId;
    }
}
