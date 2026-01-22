package com.kidari.event.domain.coin.service.dto;

import com.kidari.event.common.api.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventCoinStatResponseDto {

    private Integer remainCoin;
    private PageResponse<UserCoinBalanceStatResponseDto> userCoinBalanceList;


}
