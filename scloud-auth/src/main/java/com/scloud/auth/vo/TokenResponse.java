package com.scloud.auth.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private Long userId;
    private String username;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
