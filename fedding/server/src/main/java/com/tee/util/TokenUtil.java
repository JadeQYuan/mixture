package com.tee.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TokenUtil {

    private static ThreadLocal<Integer> token = new ThreadLocal<>();

    // 生成token：先base64加密，再反序，再拼接时间戳，再base64
    public static String generateToken(String content) {
        // 第一步：base64加密
        String firstBase64 = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        // 第二步：反序
        String reversed = new StringBuilder(firstBase64).reverse().toString();
        // 第三步：拼接当前时间戳
        String reversedWithTs = reversed + System.currentTimeMillis();
        // 第四步：再base64加密，去掉=
        return Base64.getEncoder().encodeToString(reversedWithTs.getBytes(StandardCharsets.UTF_8)).replaceAll("=+$", "");
    }

    // 解密token：先base64解码，再去除时间戳，再反序，再base64解码
    public static String getSubject(String token) {
        // 第一步：base64解码
        byte[] reversedWithTsBytes = Base64.getDecoder().decode(token);
        String reversedWithTs = new String(reversedWithTsBytes, StandardCharsets.UTF_8);
        // 第二步：去除末尾13位时间戳
        String reversed = reversedWithTs.substring(0, reversedWithTs.length() - 13);
        // 第三步：反序
        String firstBase64 = new StringBuilder(reversed).reverse().toString();
        // 第四步：base64解码
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
