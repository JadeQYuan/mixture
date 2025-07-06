package com.tee.util;

import java.util.UUID;

public class UIdUtil {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
