package com.scloud.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private final SecretKey secretKey;

    public JwtUtils(String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Long userId, String username, Duration ttl) {
        return createToken(userId, username, SecurityConstants.ACCESS_TOKEN, ttl);
    }

    public String createToken(Long userId, String username, String tokenType, Duration ttl) {
        Date now = new Date();
        return Jwts.builder().subject(String.valueOf(userId))
                .claims(Map.of("username", username, SecurityConstants.TOKEN_TYPE, tokenType))
                .issuedAt(now).expiration(new Date(now.getTime() + ttl.toMillis())).signWith(secretKey).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
