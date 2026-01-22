package com.kidari.event.common.api;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BusinessResultResponse<T> {

    private boolean isSuccess;
    private String message;
    private T data;

    public static <T> BusinessResultResponse<T> success(String message, T data) {
        return BusinessResultResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BusinessResultResponse<T> success(String message) {
        return BusinessResultResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .build();
    }

    public static <T> BusinessResultResponse<T> fail(String message, T data) {
        return BusinessResultResponse.<T>builder()
                .isSuccess(false)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BusinessResultResponse<T> fail(String message) {
        return BusinessResultResponse.<T>builder()
                .isSuccess(false)
                .message(message)
                .build();
    }

}
