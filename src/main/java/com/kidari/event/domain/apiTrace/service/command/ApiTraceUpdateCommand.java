package com.kidari.event.domain.apiTrace.service.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiTraceUpdateCommand {
    private final String traceId;
    private final Integer statusCode;
    private final String requestData;
    private final String responseData;
    private final String errorMsg;
}
