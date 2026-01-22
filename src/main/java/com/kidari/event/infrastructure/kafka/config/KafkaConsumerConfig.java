package com.kidari.event.infrastructure.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    /**
     * Kafka 리스너 에러 핸들러 설정
     * 1회 재시도, 1초 대기
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        // 1초(1000ms) 간격으로 최대 1회 재시도 (총 2번 실행: 초기 1회 + 재시도 1회)
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 1L));
        
        // 재시도 소진 후 최종 실패 시 로깅 (선택 사항)
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("Kafka Retry Failed. attempt: {}, topic: {}, key: {}", 
                    deliveryAttempt, record.topic(), record.key());
        });

        return errorHandler;
    }
}
