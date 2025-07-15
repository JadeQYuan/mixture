package com.tee.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;


public class JwtUtils {
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 设置tookie的有效期为（7天）

    public static String generateToken(String content) {
        return Jwts.builder()
                .setSubject(content)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, ConfigUtil.getPropertyFromEnvironment("face.secret.key"))
                .compact();
    }

    public static String getSubject(String token) {
        return Jwts.parser()
                .setSigningKey(ConfigUtil.getPropertyFromEnvironment("face.secret.key"))
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

}
