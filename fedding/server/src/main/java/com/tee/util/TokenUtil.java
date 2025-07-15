package com.tee.util;

public class TokenUtil {

    private static ThreadLocal<Integer> token = new ThreadLocal<>();

    public static void setToken(Integer userId) {
        token.set(userId);
    }

    public static Integer getToken() {
        return token.get();
    }
}
