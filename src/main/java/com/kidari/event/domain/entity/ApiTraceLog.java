package com.kidari.event.domain.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "tb_api_trace_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class ApiTraceLog extends BaseTimeEntity{

    @Id
    private String traceId;

    private String apiName;

    private String httpMethod;

    private Integer statusCode;

    @Column(columnDefinition = "TEXT")
    private String requestData;

    @Column(columnDefinition = "TEXT")
    private String responseData;

    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    @Builder
    public ApiTraceLog(String traceId, String apiName, String httpMethod, Integer statusCode, String requestData, String responseData, String errorMsg) {
        this.traceId = traceId;
        this.apiName = apiName;
        this.httpMethod = httpMethod;
        this.statusCode = statusCode;
        this.requestData = requestData;
        this.responseData = responseData;
        this.errorMsg = errorMsg;
    }



    public void update(Integer statusCode, String responseData, String errorMsg) {
        this.statusCode = statusCode;
        this.responseData = responseData;
        this.errorMsg = errorMsg;

    }

}
