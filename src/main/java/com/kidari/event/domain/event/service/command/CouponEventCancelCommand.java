package com.kidari.event.domain.event.service.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CouponEventCancelCommand {
    private Long eventApplicationId;
}
