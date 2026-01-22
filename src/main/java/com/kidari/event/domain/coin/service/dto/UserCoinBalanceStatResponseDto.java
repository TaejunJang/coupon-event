package com.kidari.event.domain.coin.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCoinBalanceStatResponseDto {

    private String userId;
    private Long eventId;
    private String eventName;
    private Long coin;

}
