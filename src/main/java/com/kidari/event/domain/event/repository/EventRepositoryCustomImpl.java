package com.kidari.event.domain.event.repository;


import com.kidari.event.domain.coin.service.command.EventCoinStatInquiryCommand;
import com.kidari.event.domain.event.entity.QEvent;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;

import static com.kidari.event.domain.event.entity.QEvent.event;

@AllArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Integer getEventRemainCoinStat(EventCoinStatInquiryCommand command) {
        Integer result = jpaQueryFactory
                .select(
                        event.totalCoinLimit.subtract(event.issuedCoinCount).sum().coalesce(0)
                )
                .from(event)
                .where(eqEventId(command.getEventId()))
                .fetchOne();

        return result != null ? result : 0;

    }

    private BooleanExpression eqEventId(Long eventId) {
        return eventId != null ? QEvent.event.id.eq(eventId) : null;
    }
}
