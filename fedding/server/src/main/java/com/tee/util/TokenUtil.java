package com.tee.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

public class TokenUtil {

    private static ThreadLocal<Integer> token = new ThreadLocal<>();

    // 生成token：先base64加密，再反序，再base64加密并去掉=
    public static String generateToken(String content) {
        // 第一步：base64加密
        String firstBase64 = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        // 第二步：反序
        String reversed = new StringBuilder(firstBase64).reverse().toString();
        // 第三步：再base64加密，去掉=
        String secondBase64 = Base64.getEncoder().encodeToString(reversed.getBytes(StandardCharsets.UTF_8)).replaceAll("=+$", "");
        return secondBase64;
    }

    // 解密token：先base64解码，再反序，再base64解码
    public static String getSubject(String token) {
        // 第一步：base64解码
        byte[] reversedBytes = Base64.getDecoder().decode(token);
        String reversed = new String(reversedBytes, StandardCharsets.UTF_8);
        // 第二步：反序
        String firstBase64 = new StringBuilder(reversed).reverse().toString();
        // 第三步：base64解码
        byte[] contentBytes = Base64.getDecoder().decode(firstBase64);
        return new String(contentBytes, StandardCharsets.UTF_8);
    }

    public static void setToken(Integer userId) {
        token.set(userId);
    }

    public static Integer getToken() {
        return token.get();
    }
}
