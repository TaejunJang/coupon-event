package com.kidari.event.domain.coin.service.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EventCoinStatInquiryCommand {

    private final Long eventId;


}
