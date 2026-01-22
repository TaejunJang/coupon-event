package com.kidari.event.domain.apiTrace.repository;

import com.kidari.event.domain.apiTrace.entity.ApiTraceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiTraceLogRepository extends JpaRepository<ApiTraceLog, Long> {

    ApiTraceLog findByTraceId(String traceId);

}
