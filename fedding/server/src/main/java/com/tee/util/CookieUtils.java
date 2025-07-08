package com.tee.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static void setCookie(String userId, HttpServletResponse response) {
        String jwtToken = JwtUtils.generateToken(userId);
        Cookie authCookie = new Cookie("authToken", jwtToken); // 将JWT设置为Cookie的值
        authCookie.setHttpOnly(true); // 防止客户端脚本访问Cookie
        authCookie.setPath("/"); // 设置路径为根路径
        response.addCookie(authCookie); // 将Cookie添加到响应中
    }
}
