package com.scloud.gateway.filter;

import com.scloud.gateway.security.GatewayJwtUtils;
import com.scloud.gateway.security.GatewaySecurityConstants;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String[] WHITE_EXACT_PATHS = {
            "/auth/login",
            "/auth/register",
            "/auth/refresh"
    };

    private static final String[] WHITE_PREFIX_PATHS = {
            "/doc.html",
            "/webjars",
            "/favicon.ico",
            "/swagger-resources",
            "/swagger-ui",
            "/v2/api-docs",
            "/v3/api-docs"
    };

    @Value("${scloud.gateway.jwt-secret:scloud-jwt-secret-must-be-at-least-32-bytes}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders().getFirst(GatewaySecurityConstants.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(GatewaySecurityConstants.BEARER)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            Claims claims = new GatewayJwtUtils(jwtSecret).parse(authorization.substring(GatewaySecurityConstants.BEARER.length()));
            if (!GatewaySecurityConstants.ACCESS_TOKEN.equals(String.valueOf(claims.get(GatewaySecurityConstants.TOKEN_TYPE)))) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange.mutate().request(exchange.getRequest().mutate()
                    .header(GatewaySecurityConstants.USER_ID, claims.getSubject())
                    .header(GatewaySecurityConstants.USERNAME, String.valueOf(claims.get("username"))).build()).build());
        } catch (RuntimeException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isWhitePath(String path) {
        for (String whitePath : WHITE_EXACT_PATHS) {
            if (path.equals(whitePath)) {
                return true;
            }
        }
        for (String whitePath : WHITE_PREFIX_PATHS) {
            if (path.startsWith(whitePath)) {
                return true;
            }
        }
        return false;
    }
}
