package com.kidari.event.domain.coin.repository;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.coin.service.command.EventCoinStatInquiryCommand;
import com.kidari.event.domain.coin.service.dto.UserCoinBalanceStatResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoinIssuanceRepositoryCustom {
    /**
     * 동적 조건을 이용한 코인 잔액 조회
     * (조건이 null이면 무시)
     */
    long countCoinBalanceByDynamicConditions(Long eventId, Long memberId, Constant.CoinStatus status);

    Page<UserCoinBalanceStatResponseDto> getUserCoinBalanceStat(EventCoinStatInquiryCommand eventCoinStatInquiryCommand, Pageable pageable);
}
