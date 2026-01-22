package com.kidari.event.domain.coin.service.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoinIssueCommand {
    private final String traceId;
    private final String userId;
    private final Long eventId;
}
