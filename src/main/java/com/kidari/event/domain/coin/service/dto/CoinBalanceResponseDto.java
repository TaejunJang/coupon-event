package com.kidari.event.domain.coin.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoinBalanceResponseDto {

    private final Integer balance;

}
