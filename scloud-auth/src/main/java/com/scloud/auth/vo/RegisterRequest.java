package com.scloud.auth.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String nickname;
    private String mobile;
    private String email;
}
