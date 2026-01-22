package com.kidari.event.common.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kidari.event.common.global.CachedBodyHttpServletRequest;
import com.kidari.event.common.util.RandomStringUtils;
import com.kidari.event.domain.apiTrace.service.ApiTraceLogService;
import com.kidari.event.domain.apiTrace.service.command.ApiTraceSaveCommand;
import com.kidari.event.domain.apiTrace.service.command.ApiTraceUpdateCommand;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiTraceFilter extends OncePerRequestFilter {

    private final ApiTraceLogService apiTraceLogService;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = RandomStringUtils.generateUUID();
        MDC.put("traceId", traceId);

        // 요청 바디 캐싱을 위한 커스텀 래퍼
        CachedBodyHttpServletRequest requestWrapper = new CachedBodyHttpServletRequest(request);
        // 응답 캐싱을 위한 래퍼
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // 1. 요청 진입 시점에 즉시 INSERT
        try {
            ApiTraceSaveCommand saveCommand = ApiTraceSaveCommand.builder()
                    .traceId(traceId)
                    .apiName(request.getMethod() + " " + request.getRequestURI())
                    .httpMethod(request.getMethod())
                    .requestData(requestWrapper.getBody())
                    .build();
            apiTraceLogService.saveApiTraceLog(saveCommand);
        } catch (Exception e) {
            log.error("ApiTraceLog INSERT failed", e);
        }

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {

            int status = responseWrapper.getStatus();
            String responseBody = getResponseBody(responseWrapper);
            String errorMsg = null;

            // 1. 상태 코드가 400 이상일 때만 에러 메시지 추출
            if (status >= 400 && responseBody != null) {
                errorMsg = extractMessageFromJson(responseBody);
            }

            // 2. 로그 업데이트 (UPDATE)
            try {
                ApiTraceUpdateCommand updateCommand = ApiTraceUpdateCommand.builder()
                        .traceId(traceId)
                        .statusCode(status)
                        .responseData(responseBody)
                        .errorMsg(errorMsg)
                        .build();

                apiTraceLogService.updateApiTraceLog(updateCommand);
            } catch (Exception e) {
                log.error("ApiTraceLog UPDATE failed", e);
            }

            // 3. 캐시된 바디를 실제 클라이언트 응답으로 복사
            responseWrapper.copyBodyToResponse();
            MDC.remove("traceId");

        }


    }

    private String extractMessageFromJson(String json) {
        try {
            // responseBody(JSON)를 트리 구조로 읽어서 "message" 필드만 획득
            JsonNode root = objectMapper.readTree(json);
            return root.path("message").asText(null);
        } catch (Exception e) {
            // JSON 파싱 실패 시 (예: HTML 에러 페이지 등) 원문 그대로 혹은 에러 표기
            return "JSON Parsing Error or Non-JSON Response";
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }
}
