package com.scloud.common.security;

public interface PermissionService {
    boolean hasPermission(Long userId, String permission);
}
