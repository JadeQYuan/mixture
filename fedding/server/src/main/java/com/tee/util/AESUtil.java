package com.tee.util;

import com.tee.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
public class AESUtil {

    private static final String AES = "AES";
    private static final int KEY_SIZE = 128; // 可以是 128, 192 或 256

    public static String encrypt(String data) {
        try {
            String key = ConfigUtil.getPropertyFromEnvironment("aes.secret.key");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("encrypt failed.", e);
            throw new AppException("加密失败");
        }

    }

    public static String decrypt(String data) {
        try {
            String key = ConfigUtil.getPropertyFromEnvironment("aes.secret.key");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("decrypt failed.", e);
            throw new AppException("解密失败");
        }
    }
}
