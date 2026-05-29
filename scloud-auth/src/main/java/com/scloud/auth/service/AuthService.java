package com.scloud.auth.service;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scloud.auth.entity.SysUserDO;
import com.scloud.auth.mapper.SysUserMapper;
import com.scloud.auth.vo.LoginRequest;
import com.scloud.auth.vo.RegisterRequest;
import com.scloud.auth.vo.TokenResponse;
import com.scloud.common.core.BizException;
import com.scloud.common.core.ErrorCode;
import com.scloud.common.security.JwtUtils;
import com.scloud.common.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SysUserMapper userMapper;
    @Value("${scloud.auth.jwt-secret:scloud-jwt-secret-must-be-at-least-32-bytes}")
    private String jwtSecret;

    public TokenResponse login(LoginRequest request) {
        SysUserDO user = userMapper.selectOne(Wrappers.<SysUserDO>lambdaQuery()
                .eq(SysUserDO::getUsername, request.getAccount()).or()
                .eq(SysUserDO::getMobile, request.getAccount()).or()
                .eq(SysUserDO::getEmail, request.getAccount()));
        if (user == null || !SecureUtil.sha256(request.getPassword()).equals(user.getPassword())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ErrorCode.FORBIDDEN, "账号已被禁用");
        }
        return token(user);
    }

    public TokenResponse register(RegisterRequest request) {
        SysUserDO user = new SysUserDO();
        user.setUsername(request.getUsername());
        user.setPassword(SecureUtil.sha256(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setDeptId(1L);
        userMapper.insert(user);
        return token(user);
    }

    public TokenResponse refresh(String refreshToken) {
        JwtUtils jwtUtils = new JwtUtils(jwtSecret);
        Claims claims = jwtUtils.parse(refreshToken);
        if (!SecurityConstants.REFRESH_TOKEN.equals(String.valueOf(claims.get(SecurityConstants.TOKEN_TYPE)))) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "refreshToken 无效");
        }
        SysUserDO user = userMapper.selectById(Long.valueOf(claims.getSubject()));
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        return token(user);
    }

    private TokenResponse token(SysUserDO user) {
        JwtUtils jwtUtils = new JwtUtils(jwtSecret);
        return TokenResponse.builder()
                .userId(user.getId()).username(user.getUsername())
                .accessToken(jwtUtils.createToken(user.getId(), user.getUsername(), SecurityConstants.ACCESS_TOKEN, Duration.ofHours(2)))
                .refreshToken(jwtUtils.createToken(user.getId(), user.getUsername(), SecurityConstants.REFRESH_TOKEN, Duration.ofDays(7)))
                .expiresIn(Duration.ofHours(2).toSeconds()).build();
    }
}
