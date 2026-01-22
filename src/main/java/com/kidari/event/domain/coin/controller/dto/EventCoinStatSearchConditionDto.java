package com.kidari.event.domain.coin.controller.dto;

import com.kidari.event.domain.coin.service.command.EventCoinStatInquiryCommand;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCoinStatSearchConditionDto {

    private Long eventId;

    public EventCoinStatInquiryCommand toCommand() {
       return EventCoinStatInquiryCommand.builder().eventId(this.eventId).build();

    }

}
