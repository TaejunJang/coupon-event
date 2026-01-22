package com.kidari.event.domain.event.controller;

import com.kidari.event.common.api.CommonResponse;
import com.kidari.event.common.api.PageResponse;
import com.kidari.event.domain.event.controller.dto.CouponEventApplyRequestDto;
import com.kidari.event.domain.event.controller.dto.EventApplicationSearchConditionDto;
import com.kidari.event.domain.event.service.EventApplicationService;
import com.kidari.event.domain.event.service.EventService;
import com.kidari.event.domain.event.service.command.CouponEventCancelCommand;
import com.kidari.event.domain.event.service.dto.UserEventApplicationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventApplicationService eventApplicationService;

    //소유한 응모 코인으로 특정 휴가쿠폰 응모요청 API
    @PostMapping("/coupons")
    public CommonResponse<String> applyCouponEvent(@Valid @RequestBody CouponEventApplyRequestDto request) {
        eventApplicationService.applyCouponEvent(request.toCommand());
        return CommonResponse.success("쿠폰응모 신청 요청 완료");
    }

    //휴가쿠폰 응모 이벤트 취소 API
    @PatchMapping("/coupons/{eventApplicationId}/cancel")
    public CommonResponse<String> cancelCouponEvent(@PathVariable("eventApplicationId") Long eventApplicationId) {

        CouponEventCancelCommand couponEventCancelCommand = CouponEventCancelCommand.builder()
                .eventApplicationId(eventApplicationId)
                .build();

        return CommonResponse.success(eventApplicationService.cancelCouponEvent(couponEventCancelCommand));

    }

    //특정 사용자의 쿠폰이벤트 응모현황 조회 API
    @GetMapping("/applications")
    public CommonResponse<PageResponse<UserEventApplicationResponseDto>> getMyEventApplications(
            @Valid @ModelAttribute EventApplicationSearchConditionDto eventApplicationSearchConditionDto
            , @PageableDefault(size = 20)Pageable pageable
            ) {

        return CommonResponse.success(eventApplicationService.getMyEventApplications(eventApplicationSearchConditionDto.toCommand(), pageable));

    }



}
