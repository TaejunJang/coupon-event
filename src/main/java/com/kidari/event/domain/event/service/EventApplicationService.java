package com.kidari.event.domain.event.service;

import com.kidari.event.common.api.BusinessResultResponse;
import com.kidari.event.common.Constant;
import com.kidari.event.common.api.PageResponse;
import com.kidari.event.domain.coin.repository.CoinIssuanceRepository;
import com.kidari.event.domain.coupon.repository.CouponRepository;
import com.kidari.event.domain.entity.*;
import com.kidari.event.domain.event.repository.EventApplicationRepository;
import com.kidari.event.domain.event.repository.EventRepository;
import com.kidari.event.domain.event.service.command.CouponEventApplyCommand;
import com.kidari.event.domain.event.service.command.CouponEventCancelCommand;
import com.kidari.event.domain.event.service.command.RegistCouponEventCommand;
import com.kidari.event.domain.event.service.command.UserEventApplicationInquiryCommand;
import com.kidari.event.domain.event.service.dto.UserEventApplicationResponseDto;
import com.kidari.event.domain.member.repository.MemberRepository;
import com.kidari.event.domain.port.out.CouponApplyEvent;
import com.kidari.event.domain.port.out.EventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventApplicationService {


    private final CoinIssuanceRepository coinIssuanceRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final EventPort eventPort;

    public void applyCouponEvent(CouponEventApplyCommand command) {
        log.info("Requesting coupon event apply for member: {}, eventId: {}", command.getUserId(), command.getEventId());

        CouponApplyEvent event = CouponApplyEvent.builder()
                .traceId(MDC.get("traceId"))
                .userId(command.getUserId())
                .eventId(command.getEventId())
                .couponId(command.getCouponId())
                .eventTime(LocalDateTime.now())
                .build();

        eventPort.sendCouponEventApplyRequest(event);
    }


    @Transactional
    public BusinessResultResponse<Void> registCouponEvent(RegistCouponEventCommand command) {

        long appliedCnt = eventApplicationRepository.countByRequestId(command.getTraceId());

        if (appliedCnt > 0) {
            return BusinessResultResponse.fail("이미 응모 완료된 요청입니다.");
        }


        Member member = memberRepository.findByUserId(command.getUserId());

        List<CoinIssuance> issuedCoinList = coinIssuanceRepository.findByEventIdAndMemberIdAndStatus(command.getEventId(),member.getId(), Constant.CoinStatus.AVAILABLE);

        if (issuedCoinList.isEmpty()) {
            return BusinessResultResponse.fail("응모 가능한 코인이 모두 소진됐습니다.");
        }

        Optional<Event> event = eventRepository.findByIdAndStatus(command.getEventId(), Constant.EventStatus.ACTIVE);
        Optional<Coupon> coupon = couponRepository.findById(command.getCouponId());

        //유저가 가지고있는 발행된 코인중 한개를 사용완료로 변경처리
        CoinIssuance issuedCoin = issuedCoinList.get(0);
        issuedCoin.use();

        EventApplication eventApplication = EventApplication.builder()
                .event(event.get())
                .member(member)
                .coupon(coupon.get())
                .coinIssuance(issuedCoin)
                .requestId(command.getTraceId())
                .build();


        eventApplicationRepository.save(eventApplication);

        return BusinessResultResponse.success("응모 완료");
    }

    @Transactional
    public String cancelCouponEvent(CouponEventCancelCommand command) {

        //응모쿠폰이벤트 회수가 추첨일 전까지는 회수가 가능한지 체크
        Optional<EventApplication> eventApplication = eventApplicationRepository.findById(command.getEventApplicationId());

        Event event = eventApplication.get().getEvent();

        LocalDateTime cancelTime = LocalDateTime.now();

        if (cancelTime.isAfter(event.getDrawAt())) {
           return "추첨 기간으로 인해 취소가 불가능합니다.";
        };

        //쿠폰이벤트 응모 상태변경
        eventApplication.get().cancel();

        //사용된 코인 상태 사용가능으로 변경
        eventApplication.get().getCoinIssuance().release();

        return "쿠몬응모 취소완료";
    }


    public PageResponse<UserEventApplicationResponseDto> getMyEventApplications(UserEventApplicationInquiryCommand command, Pageable pageable) {

        return PageResponse.of(eventApplicationRepository.findUserApplicationHistory(command,pageable));
    }

}
