package com.kidari.event.domain.coupon.service.command;

import com.kidari.event.common.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CouponApplicationStatInquiryCommand {

    private final String couponGroupCode;
    private final Constant.CouponType couponType;
    private final Constant.ApplicationStatus applicationStatus;

}
