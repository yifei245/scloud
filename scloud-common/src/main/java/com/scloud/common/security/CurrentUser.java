package com.scloud.common.security;

import lombok.Data;

@Data
public class CurrentUser {
    private Long userId;
    private String username;
}
