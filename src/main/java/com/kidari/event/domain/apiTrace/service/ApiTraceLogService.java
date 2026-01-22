package com.kidari.event.domain.apiTrace.service;

import com.kidari.event.domain.apiTrace.entity.ApiTraceLog;
import com.kidari.event.domain.apiTrace.repository.ApiTraceLogRepository;
import com.kidari.event.domain.apiTrace.service.command.ApiTraceSaveCommand;
import com.kidari.event.domain.apiTrace.service.command.ApiTraceUpdateCommand;
import com.kidari.event.domain.apiTrace.service.dto.ApiTraceLogResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiTraceLogService {

    private final ApiTraceLogRepository apiTraceLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveApiTraceLog(ApiTraceSaveCommand command) {

        ApiTraceLog apiTraceLog = ApiTraceLog.builder()
                .apiName(command.getApiName())
                .traceId(command.getTraceId())
                .requestData(command.getRequestData())
                .httpMethod(command.getHttpMethod())
                .build();

        apiTraceLogRepository.save(apiTraceLog);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateApiTraceLog(ApiTraceUpdateCommand command) {

       ApiTraceLog apiTraceLog = apiTraceLogRepository.findByTraceId(command.getTraceId());

       if (apiTraceLog != null) {
           apiTraceLog.update(command.getStatusCode(), command.getResponseData(), command.getErrorMsg());
           // JPA 변경 감지(Dirty Checking)가 동작하겠지만, 명시적 save 호출도 무방함 (save는 merge 혹은 persist)
           // 여기서는 기존 코드 유지: apiTraceLogRepository.save(apiTraceLog);
       } else {
           log.warn("ApiTraceLog not found for update. traceId: {}", command.getTraceId());
       }
    }


    public ApiTraceLogResponseDto getApiTraceLog(String traceId) {
        ApiTraceLog apiTraceLog = apiTraceLogRepository.findByTraceId(traceId);

        if (apiTraceLog == null) {
            throw new IllegalArgumentException("Trace log not found for traceId: " + traceId);
        }

        return ApiTraceLogResponseDto.toDto(apiTraceLog);
    }

}
