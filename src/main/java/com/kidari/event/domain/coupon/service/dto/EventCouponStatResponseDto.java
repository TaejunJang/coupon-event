package com.kidari.event.domain.coupon.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventCouponStatResponseDto {

    private Long couponId;
    private String eventName;
    private String couponName;
    private String userId;
    private String userName;
    private LocalDateTime appliedAt;
    private LocalDateTime canceledAt;

}
