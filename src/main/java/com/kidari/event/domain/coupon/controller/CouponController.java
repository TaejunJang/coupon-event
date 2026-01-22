package com.kidari.event.domain.coupon.controller;

import com.kidari.event.common.api.CommonResponse;
import com.kidari.event.common.api.PageResponse;
import com.kidari.event.domain.coupon.controller.dto.CouponApplicationStatSearchConditionDto;
import com.kidari.event.domain.coupon.service.CouponService;
import com.kidari.event.domain.coupon.service.dto.EventCouponStatResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    //휴가 쿠폰별 전체 응모 현황을 조회 API
    @GetMapping("/events/stats")
    public CommonResponse<PageResponse<EventCouponStatResponseDto>> getCouponApplicationStat(
            @Valid @ModelAttribute CouponApplicationStatSearchConditionDto couponApplicationStatRequestDto
            , @PageableDefault(size = 20) Pageable pageable
    ) {

        return CommonResponse.success(couponService.getEventCouponStats(couponApplicationStatRequestDto.toCommand(), pageable));

    }

}
