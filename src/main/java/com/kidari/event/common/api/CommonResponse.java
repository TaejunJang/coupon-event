package com.kidari.event.common.api;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommonResponse<T> {
    private final String code;
    private final String message;
    private final LocalDateTime time;
    private final T data;

    private CommonResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.time = LocalDateTime.now();
        this.data = data;
    }

    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>("SUCCESS", "요청성공", null);
    }

    public static <T> CommonResponse<T> success(String message) {
        return new CommonResponse<>("SUCCESS", message, null);
    }

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>("SUCCESS", "요청 성공", data);
    }

    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>("SUCCESS", message, data);
    }

    public static CommonResponse<Void> error(String code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
