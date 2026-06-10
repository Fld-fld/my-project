package com.example.express.util;

import java.util.Random;

/**
 * 取件码生成工具
 */
public class PickupCodeUtil {
    
    private static final Random random = new Random();
    
    /**
     * 生成6位数字取件码
     * 规则：时间戳后4位 + 随机2位
     */
    public static String generateCode() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String timePart = timestamp.substring(timestamp.length() - 4);
        String randomPart = String.format("%02d", random.nextInt(100));
        return timePart + randomPart;
    }
    
    /**
     * 验证取件码格式
     */
    public static boolean isValidCode(String code) {
        if (code == null || code.length() != 6) {
            return false;
        }
        return code.matches("\\d{6}");
    }
}
