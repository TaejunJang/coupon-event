package com.kidari.event.domain.event.repository;

import com.kidari.event.domain.coin.service.command.EventCoinStatInquiryCommand;

public interface EventRepositoryCustom {

    Integer getEventRemainCoinStat(EventCoinStatInquiryCommand eventCoinStatInquiryCommand);

}
