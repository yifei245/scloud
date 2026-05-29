package com.scloud.common.security;

import com.scloud.common.core.BizException;
import com.scloud.common.core.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    private final ObjectProvider<PermissionService> permissionServiceProvider;

    public SecurityInterceptor(ObjectProvider<PermissionService> permissionServiceProvider) {
        this.permissionServiceProvider = permissionServiceProvider;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        CurrentUser user = new CurrentUser();
        String userId = request.getHeader(SecurityConstants.USER_ID);
        if (userId != null) {
            user.setUserId(Long.valueOf(userId));
        }
        user.setUsername(request.getHeader(SecurityConstants.USERNAME));
        CurrentUserContext.set(user);

        RequirePermission permission = findPermission(handler);
        if (permission == null) {
            return true;
        }
        if (user.getUserId() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        PermissionService permissionService = permissionServiceProvider.getIfAvailable();
        if (permissionService == null) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
        if (!permissionService.hasPermission(user.getUserId(), permission.value())) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler, Exception ex) {
        CurrentUserContext.clear();
    }

    private RequirePermission findPermission(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return null;
        }
        RequirePermission permission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (permission != null) {
            return permission;
        }
        return handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
    }
}
