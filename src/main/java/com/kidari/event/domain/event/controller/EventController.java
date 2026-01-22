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

    /**
     * 쿠폰 응모 요청 API
     *
     * 사용자가 보유한 코인을 사용하여 특정 이벤트 쿠폰에 응모합니다.
     *
     * @param request 쿠폰 응모 요청 정보 (사용자 ID, 이벤트 ID, 쿠폰 ID)
     * @return 성공 메시지
     */
    @PostMapping("/coupons")
    public CommonResponse<String> applyCouponEvent(@Valid @RequestBody CouponEventApplyRequestDto request) {
        eventApplicationService.applyCouponEvent(request.toCommand());
        return CommonResponse.success("쿠폰응모 신청 요청 완료");
    }

    /**
     * 쿠폰 응모 취소 API
     *
     * 기존에 신청한 쿠폰 응모 내역을 취소하고 사용된 코인을 반환합니다.
     *
     * @param eventApplicationId 취소할 이벤트 응모 ID
     * @return 성공 메시지 (취소 완료)
     */
    @PatchMapping("/coupons/{eventApplicationId}/cancel")
    public CommonResponse<String> cancelCouponEvent(@PathVariable("eventApplicationId") Long eventApplicationId) {

        CouponEventCancelCommand couponEventCancelCommand = CouponEventCancelCommand.builder()
                .eventApplicationId(eventApplicationId)
                .build();

        return CommonResponse.success(eventApplicationService.cancelCouponEvent(couponEventCancelCommand));

    }

    /**
     * 내 응모 현황 조회 API
     *
     * 특정 사용자의 쿠폰 이벤트 응모 내역을 페이징하여 조회합니다.
     *
     * @param eventApplicationSearchConditionDto 검색 조건 (사용자 ID 등)
     * @param pageable 페이징 정보
     * @return 사용자 응모 내역 리스트 (페이징 포함)
     */
    @GetMapping("/applications")
    public CommonResponse<PageResponse<UserEventApplicationResponseDto>> getMyEventApplications(
            @Valid @ModelAttribute EventApplicationSearchConditionDto eventApplicationSearchConditionDto
            , @PageableDefault(size = 20)Pageable pageable
            ) {

        return CommonResponse.success(eventApplicationService.getMyEventApplications(eventApplicationSearchConditionDto.toCommand(), pageable));

    }



}
