package com.kidari.event.domain.coupon.repository;

import com.kidari.event.domain.coupon.service.command.CouponApplicationStatInquiryCommand;
import com.kidari.event.domain.coupon.service.dto.EventCouponStatResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryCustom {

    Page<EventCouponStatResponseDto> getEventCouponStat(CouponApplicationStatInquiryCommand couponApplicationStatInquiryCommand, Pageable pageable);

}
