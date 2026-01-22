package com.kidari.event.domain.coin.repository;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.coin.service.command.EventCoinStatInquiryCommand;
import com.kidari.event.domain.coin.service.dto.UserCoinBalanceStatResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.kidari.event.domain.coin.entity.QCoinIssuance.coinIssuance;
import static com.kidari.event.domain.event.entity.QEvent.event;
import static com.kidari.event.domain.member.entity.QMember.member;

@RequiredArgsConstructor
public class CoinIssuanceRepositoryCustomImpl implements CoinIssuanceRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long countCoinBalanceByDynamicConditions(Long eventId, Long memberId, Constant.CoinStatus status) {
        Long count = jpaQueryFactory
                .select(coinIssuance.count())
                .from(coinIssuance)
                .where(
                        eqEventId(eventId),
                        eqMemberId(memberId),
                        eqStatus(status)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public Page<UserCoinBalanceStatResponseDto> getUserCoinBalanceStat(EventCoinStatInquiryCommand command, Pageable pageable) {
        List<UserCoinBalanceStatResponseDto> content = jpaQueryFactory
                .select(
                        Projections.fields(UserCoinBalanceStatResponseDto.class
                                ,member.userId
                                ,event.id.as("eventId")
                                ,event.title.as("eventName")
                                ,coinIssuance.count().as("coin")
                        ))
                .from(coinIssuance)
                .join(coinIssuance.member, member)
                .join(coinIssuance.event, event)
                .where(
                        eqEventId(command.getEventId())
                )
                .groupBy(coinIssuance.event.id, coinIssuance.member.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(coinIssuance.count())
                .from(coinIssuance)
                .join(coinIssuance.member, member)
                .join(coinIssuance.event, event)
                .where(
                        eqEventId(command.getEventId())
                )
                .groupBy(coinIssuance.event.id, coinIssuance.member.id);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqEventId(Long eventId) {
        return eventId != null ? coinIssuance.event.id.eq(eventId) : null;
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return memberId != null ? coinIssuance.member.id.eq(memberId) : null;
    }

    private BooleanExpression eqStatus(Constant.CoinStatus status) {
        return status != null ? coinIssuance.status.eq(status) : null;
    }
}
