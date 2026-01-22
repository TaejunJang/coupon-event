package com.kidari.event.domain.port.out;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CoinAcquisitionEvent {
    private String traceId;
    private String userId;
    private Long eventId;
    private LocalDateTime eventTime;

    @Builder
    public CoinAcquisitionEvent(String traceId, String userId, Long eventId, LocalDateTime eventTime) {
        this.traceId = traceId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventTime = eventTime;
    }
}
