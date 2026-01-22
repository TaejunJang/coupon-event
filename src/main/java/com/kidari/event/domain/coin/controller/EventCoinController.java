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

    //쿠폰이벤트에 응모할 코인획득 요청 API
    @PostMapping("/acquisitions")
    public CommonResponse<String> requestEventCoin(@Valid @RequestBody CoinIssuanceRequestDto request) {

        coinService.requestEventCoin(request.toCommand());
        return CommonResponse.success("응모코인 신청 요청 완료");
    }

    //사용자가 가진 응모코인 수량 조회 api
    @GetMapping("/balances")
    public CommonResponse<CoinBalanceResponseDto> getCoinBalance(@Valid @ModelAttribute CoinBalanceRequestDto request) {

        CoinBalanceResponseDto coinBalanceResponseDto = coinService.getCoinBalance(request.toCommand());
        return CommonResponse.success("조회 성공", coinBalanceResponseDto);
    }

    //이벤트에 사용가능한 남은 응모코인 수와 사용자별 획득한 응모코인수량 조회 API
    @GetMapping("/stats")
    public CommonResponse<EventCoinStatResponseDto> getCoinStat(
            @Valid @ModelAttribute EventCoinStatSearchConditionDto eventCoinStatSearchConditionDto
            , @PageableDefault(size = 20) Pageable pageable
    ) {

        return CommonResponse.success(coinService.getEventCoinStat(eventCoinStatSearchConditionDto.toCommand(), pageable));

    }

}
