package com.scloud.common.security;

public final class CurrentUserContext {
    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    private CurrentUserContext() {
    }

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        CurrentUser user = get();
        return user == null ? null : user.getUserId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
