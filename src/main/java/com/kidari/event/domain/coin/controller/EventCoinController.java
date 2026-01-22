package com.kidari.event.domain.coin.controller;

import com.kidari.event.common.api.CommonResponse;
import com.kidari.event.domain.coin.controller.dto.CoinBalanceRequestDto;
import com.kidari.event.domain.coin.controller.dto.CoinIssuanceRequestDto;
import com.kidari.event.domain.coin.controller.dto.EventCoinStatSearchConditionDto;
import com.kidari.event.domain.coin.service.CoinService;
import com.kidari.event.domain.coin.service.dto.CoinBalanceResponseDto;
import com.kidari.event.domain.coin.service.dto.EventCoinStatResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
public class EventCoinController {

    private final CoinService coinService;

    /**
     * 응모 코인 획득 요청 API
     *
     * 쿠폰 이벤트에 참여하기 위한 코인을 획득 요청합니다. (선착순 등)
     *
     * @param request 코인 발급 요청 정보 (사용자 ID, 이벤트 ID)
     * @return 성공 메시지
     */
    @PostMapping("/acquisitions")
    public CommonResponse<String> requestEventCoin(@Valid @RequestBody CoinIssuanceRequestDto request) {

        coinService.requestEventCoin(request.toCommand());
        return CommonResponse.success("응모코인 신청 요청 완료");
    }

    /**
     * 코인 잔액 조회 API
     *
     * 특정 사용자가 보유한 현재 사용 가능한 응모 코인 수량을 조회합니다.
     *
     * @param request 코인 잔액 조회 조건
     * @return 사용자의 코인 잔여 갯수 정보
     */
    @GetMapping("/balances")
    public CommonResponse<CoinBalanceResponseDto> getCoinBalance(@Valid @ModelAttribute CoinBalanceRequestDto request) {

        CoinBalanceResponseDto coinBalanceResponseDto = coinService.getCoinBalance(request.toCommand());
        return CommonResponse.success("조회 성공", coinBalanceResponseDto);
    }

    /**
     * 이벤트 코인 현황 조회 API
     *
     * 이벤트의 전체 잔여 코인 수량과 사용자별 코인 획득 현황을 조회합니다.
     *
     * @param eventCoinStatSearchConditionDto 현황 검색 조건
     * @param pageable 페이징 정보
     * @return 이벤트 코인 통계 정보
     */
    @GetMapping("/stats")
    public CommonResponse<EventCoinStatResponseDto> getCoinStat(
            @Valid @ModelAttribute EventCoinStatSearchConditionDto eventCoinStatSearchConditionDto
            , @PageableDefault(size = 20) Pageable pageable
    ) {

        return CommonResponse.success(coinService.getEventCoinStat(eventCoinStatSearchConditionDto.toCommand(), pageable));

    }

}
