package com.kidari.event.domain.port.out;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponApplyEvent {
    private String traceId;
    private String userId;
    private Long eventId;
    private Long couponId;
    private LocalDateTime eventTime;

    @Builder
    public CouponApplyEvent(String traceId, String userId, Long eventId, Long couponId, LocalDateTime eventTime) {
        this.traceId = traceId;
        this.userId = userId;
        this.eventId = eventId;
        this.couponId = couponId;
        this.eventTime = eventTime;
    }
}
