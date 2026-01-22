package com.kidari.event.domain.coin.service.command;

import com.kidari.event.common.Constant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoinBalanceCommand {
    private final String userId;
    private final Long eventId;
    private final Constant.CoinStatus status;
}
