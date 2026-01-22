package com.kidari.event.domain.coupon.repository;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.coupon.service.command.CouponApplicationStatInquiryCommand;
import com.kidari.event.domain.coupon.service.dto.EventCouponStatResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.kidari.event.domain.coupon.entity.QCoupon.coupon;
import static com.kidari.event.domain.event.entity.QEvent.event;
import static com.kidari.event.domain.event.entity.QEventApplication.eventApplication;
import static com.kidari.event.domain.member.entity.QMember.member;

@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<EventCouponStatResponseDto> getEventCouponStat(CouponApplicationStatInquiryCommand command, Pageable pageable) {

        List<EventCouponStatResponseDto> content = jpaQueryFactory
                .select(Projections.fields(EventCouponStatResponseDto.class
                    ,coupon.id.as("couponId")
                    ,event.title.as("eventName")
                    ,coupon.couponName
                    ,member.userId
                    ,member.name.as("userName")
                    ,eventApplication.appliedAt
                    ,eventApplication.canceledAt
                ))
                .from(coupon)
                .join(coupon.event,event)
                .join(eventApplication).on(coupon.id.eq(eventApplication.coupon.id))
                .join(eventApplication.member,member)
                .where(
                        eqCouponGroupCode(command.getCouponGroupCode())
                        ,eqCouponType(command.getCouponType())
                )
                .orderBy(eventApplication.appliedAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(
                        coupon.id.count()
                )
                .from(coupon)
                .join(coupon.event,event)
                .join(eventApplication).on(coupon.id.eq(eventApplication.coupon.id))
                .join(eventApplication.member,member)
                .where(
                        eqCouponGroupCode(command.getCouponGroupCode())
                        ,eqCouponType(command.getCouponType())
                );


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqCouponGroupCode(String couponGroupCode) {
        return couponGroupCode != null ? coupon.couponGroupCode.eq(couponGroupCode) : null;
    }

    private BooleanExpression eqCouponType(Constant.CouponType couponType) {

        return couponType != null ? coupon.couponType.eq(couponType) : null;
    }
}
