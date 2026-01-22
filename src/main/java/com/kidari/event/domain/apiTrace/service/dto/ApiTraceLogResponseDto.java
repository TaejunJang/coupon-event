package com.kidari.event.domain.apiTrace.service.dto;

import com.kidari.event.domain.entity.ApiTraceLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiTraceLogResponseDto {
    private final String traceId;
    private final String userId;
    private final String apiName;
    private final String httpMethod;
    private final Integer statusCode;
    private final String requestData;
    private final String responseData;
    private final String errorMsg;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ApiTraceLogResponseDto toDto(ApiTraceLog entity) {
        return ApiTraceLogResponseDto.builder()
                                     .traceId(entity.getTraceId())
                                     .apiName(entity.getApiName())
                                     .httpMethod(entity.getHttpMethod())
                                     .statusCode(entity.getStatusCode())
                                     .requestData(entity.getRequestData())
                                     .responseData(entity.getResponseData())
                                     .errorMsg(entity.getErrorMsg())
                                     .createdAt(entity.getCreatedAt())
                                     .updatedAt(entity.getUpdatedAt())
                                     .build();
    }
}
