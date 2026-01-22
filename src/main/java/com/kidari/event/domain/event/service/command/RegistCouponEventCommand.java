package com.kidari.event.domain.event.service.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegistCouponEventCommand {

    private final String userId;
    private final Long eventId;
    private final Long couponId;
    private final String traceId;

}
