package com.kidari.event.common.util;

import java.security.SecureRandom;

public class RandomStringUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomStringUtils() {
        // 인스턴스화 방지
    }

    /**
     * 6자리의 랜덤 알판뉴메릭 문자열을 생성합니다.
     */
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    /**
     * 지정된 길이의 랜덤 알판뉴메릭 문자열을 생성합니다.
     */
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * 대시(-)가 제거된 32자리의 UUID 문자열을 생성합니다.
     */
    public static String generateUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
