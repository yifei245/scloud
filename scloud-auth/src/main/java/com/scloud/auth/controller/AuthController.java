package com.scloud.auth.controller;

import com.scloud.auth.service.AuthService;
import com.scloud.auth.vo.LoginRequest;
import com.scloud.auth.vo.RefreshRequest;
import com.scloud.auth.vo.RegisterRequest;
import com.scloud.auth.vo.TokenResponse;
import com.scloud.common.core.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "认证中心")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "登录", description = "支持用户名、手机号、邮箱登录")
    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @Operation(summary = "注册", description = "普通用户注册并返回访问令牌")
    @PostMapping("/register")
    public Result<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @Operation(summary = "刷新 Token", description = "使用 refreshToken 获取新的访问令牌")
    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return Result.ok(authService.refresh(request.getRefreshToken()));
    }
}
