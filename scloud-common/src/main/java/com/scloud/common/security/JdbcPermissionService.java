package com.scloud.common.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JdbcPermissionService implements PermissionService {
    private final ObjectProvider<DataSource> dataSourceProvider;

    public JdbcPermissionService(ObjectProvider<DataSource> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource == null) {
            return false;
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Integer adminCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM sys_user_role ur
                JOIN sys_role r ON r.id = ur.role_id
                WHERE ur.user_id = ? AND r.role_key = 'admin' AND r.status = 1
                """, Integer.class, userId);
        if (adminCount != null && adminCount > 0) {
            return true;
        }
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM sys_user_role ur
                JOIN sys_role r ON r.id = ur.role_id
                JOIN sys_role_menu rm ON rm.role_id = r.id
                JOIN sys_menu m ON m.id = rm.menu_id
                WHERE ur.user_id = ? AND r.status = 1 AND m.status = 1 AND m.permission = ?
                """, Integer.class, userId, permission);
        return count != null && count > 0;
    }
}
