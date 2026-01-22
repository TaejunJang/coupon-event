package com.kidari.event.domain.coupon.controller.dto;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.coupon.service.command.CouponApplicationStatInquiryCommand;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponApplicationStatSearchConditionDto {

    private String couponGroupCode;
    private Constant.CouponType couponType;

    public CouponApplicationStatInquiryCommand toCommand(){

        return CouponApplicationStatInquiryCommand.builder()
                                                  .couponType(this.couponType)
                                                  .couponGroupCode(this.couponGroupCode)
                                                  .build();
    }

}
