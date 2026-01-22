package com.kidari.event.domain.event.repository;


import com.kidari.event.common.Constant;
import com.kidari.event.domain.event.service.command.UserEventApplicationInquiryCommand;
import com.kidari.event.domain.event.service.dto.UserEventApplicationResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.kidari.event.domain.entity.QCoupon.coupon;
import static com.kidari.event.domain.entity.QEvent.event;
import static com.kidari.event.domain.entity.QEventApplication.eventApplication;
import static com.kidari.event.domain.entity.QMember.member;

@RequiredArgsConstructor
public class EventApplicationRepositoryCustomImpl implements EventApplicationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<UserEventApplicationResponseDto> findUserApplicationHistory(UserEventApplicationInquiryCommand command, Pageable pageable) {
        List<UserEventApplicationResponseDto> content = jpaQueryFactory
            .select(
                Projections.fields(UserEventApplicationResponseDto.class
                , eventApplication.id.as("eventApplicationId")
                , member.userId
                , event.title.as("eventTitle")
                , coupon.couponName
                , eventApplication.status.as("applicationStatus")
                , eventApplication.appliedAt
                , eventApplication.canceledAt
            ))
            .from(eventApplication)
            .join(eventApplication.event, event)
            .join(eventApplication.coupon, coupon)
            .join(eventApplication.member, member)
            .where(
                    userIdEq(command.getUserId())
                    ,statusEq(command.getApplicationStatus())
            )
            .orderBy(eventApplication.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(eventApplication.count())
                .from(eventApplication)
                .join(eventApplication.event, event)
                .join(eventApplication.coupon, coupon)
                .join(eventApplication.member, member)
                .where(
                        userIdEq(command.getUserId()),
                        statusEq(command.getApplicationStatus())
                );

        return  PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression userIdEq(String userId){
        return StringUtils.hasText(userId) ? member.userId.eq(userId) : null;
    }

    private BooleanExpression statusEq(Constant.ApplicationStatus status) {
        return status != null ? eventApplication.status.eq(status) : null;
    }
}
