package com.kidari.event.domain.event.service.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CouponEventApplyCommand {

    private final String userId;
    private final Long eventId;
    private final Long couponId;
}
