package com.scloud.common.web;

import com.scloud.common.core.TraceIdHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TraceFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            response.setHeader(TraceIdHolder.TRACE_ID, TraceIdHolder.getTraceId());
            filterChain.doFilter(request, response);
        } finally {
            org.slf4j.MDC.remove(TraceIdHolder.TRACE_ID);
        }
    }
}
