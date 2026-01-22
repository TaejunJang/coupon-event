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

    /**
     * 쿠폰별 응모 현황 조회 API
     *
     * 특정 쿠폰 그룹 및 타입에 대한 전체 사용자 응모 현황을 통계로 조회합니다.
     *
     * @param couponApplicationStatRequestDto 통계 검색 조건
     * @param pageable 페이징 정보
     * @return 쿠폰별 응모 통계 리스트
     */
    @GetMapping("/events/stats")
    public CommonResponse<PageResponse<EventCouponStatResponseDto>> getCouponApplicationStat(
            @Valid @ModelAttribute CouponApplicationStatSearchConditionDto couponApplicationStatRequestDto
            , @PageableDefault(size = 20) Pageable pageable
    ) {

        return CommonResponse.success(couponService.getEventCouponStats(couponApplicationStatRequestDto.toCommand(), pageable));

    }

}
