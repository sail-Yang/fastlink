package com.progsail.fastlink.admin.util;

import java.util.Random;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组gid随机生成器
 * @date 2024/2/17 17:32
 */
public class GIDRandomGeneratorUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 6;
    private static final Random RANDOM = new Random();

    public static String generateRandomID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
