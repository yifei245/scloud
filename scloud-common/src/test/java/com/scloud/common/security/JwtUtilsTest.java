package com.scloud.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class JwtUtilsTest {
    private final JwtUtils jwtUtils = new JwtUtils("scloud-jwt-secret-must-be-at-least-32-bytes");

    @Test
    void shouldCreateAccessTokenWithTokenType() {
        String token = jwtUtils.createToken(1L, "admin", SecurityConstants.ACCESS_TOKEN, Duration.ofMinutes(5));

        Claims claims = jwtUtils.parse(token);

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("username")).isEqualTo("admin");
        assertThat(claims.get(SecurityConstants.TOKEN_TYPE)).isEqualTo(SecurityConstants.ACCESS_TOKEN);
    }
}
