package com.file.storage.api.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenUtil {

    private static final SecureRandom random = new SecureRandom();

    private static final int RAW_BYTES = 32;
    private static final String KEY_PREFIX = "fs_secret_";
    private static final int PREFIX_LEN = 20;


    public static String generateToken() {
        byte[] bytes = new byte[RAW_BYTES];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return KEY_PREFIX + token;
    }

    public static String extractPrefix(String key) {
        if (key == null || !key.startsWith("fs_secret_")) return null;
        int end = Math.min(key.length(), 10 + PREFIX_LEN);
        if (end <= 10) return null;
        return key.substring(0, end);
    }
}
