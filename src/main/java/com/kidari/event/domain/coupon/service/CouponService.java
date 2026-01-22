package com.kidari.event.domain.coupon.service;

import com.kidari.event.common.api.PageResponse;
import com.kidari.event.domain.coupon.repository.CouponRepository;
import com.kidari.event.domain.coupon.service.command.CouponApplicationStatInquiryCommand;
import com.kidari.event.domain.coupon.service.dto.EventCouponStatResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;


    public PageResponse<EventCouponStatResponseDto> getEventCouponStats(CouponApplicationStatInquiryCommand command, Pageable pageable) {
        return PageResponse.of(couponRepository.getEventCouponStat(command, pageable));
    }


}
