package com.kidari.event.domain.apiTrace.service.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiTraceSaveCommand {

    private String traceId;
    private String apiName;
    private String httpMethod;
    private Integer statusCode;
    private String requestData;

}
