package com.kidari.event.domain.coin.service;

import com.kidari.event.common.api.BusinessResultResponse;
import com.kidari.event.common.Constant;
import com.kidari.event.common.api.PageResponse;
import com.kidari.event.domain.coin.repository.CoinIssuanceRepository;
import com.kidari.event.domain.coin.service.command.CoinBalanceCommand;
import com.kidari.event.domain.coin.service.command.CoinIssuanceCommand;
import com.kidari.event.domain.coin.service.command.CoinIssueCommand;
import com.kidari.event.domain.coin.service.command.EventCoinStatInquiryCommand;
import com.kidari.event.domain.coin.service.dto.CoinBalanceResponseDto;
import com.kidari.event.domain.coin.service.dto.EventCoinStatResponseDto;
import com.kidari.event.domain.coin.service.dto.UserCoinBalanceStatResponseDto;
import com.kidari.event.domain.entity.CoinIssuance;
import com.kidari.event.domain.entity.Event;
import com.kidari.event.domain.entity.Member;
import com.kidari.event.domain.event.repository.EventRepository;
import com.kidari.event.domain.member.repository.MemberRepository;
import com.kidari.event.domain.port.out.CoinAcquisitionEvent;
import com.kidari.event.domain.port.out.EventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CoinService {

    private final EventPort eventPort;
    private final CoinIssuanceRepository coinIssuanceRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public void requestEventCoin(CoinIssuanceCommand command) {
        log.info("Requesting event coin for member: {}, eventId: {}", command.getUserId(), command.getEventId());

        CoinAcquisitionEvent event = CoinAcquisitionEvent.builder()
                .traceId(MDC.get("traceId"))
                .userId(command.getUserId())
                .eventId(command.getEventId())
                .eventTime(LocalDateTime.now())
                .build();

        eventPort.sendCoinAcquisitionRequest(event);
    }


    public BusinessResultResponse<Void> coinIssuable(Long eventId, String userId, String issuanceKey) {
        Optional<Event> optionalEvent = eventRepository.findByIdAndStatus(eventId, Constant.EventStatus.ACTIVE);

        Integer userCoinLimit = 0;
        Integer totalCoinLimit = 0;
        Integer issuedCoinCount = 0;

        //연관 이벤트가 유효한지 체크
        if (optionalEvent.isEmpty()) {
            return BusinessResultResponse.fail("유효한 이벤트가 아닙니다.");
        }

        //재시도에 대한 멱등성을 위한 체크
        if (coinIssuanceRepository.existsByIssuanceKey(issuanceKey)) {
            return BusinessResultResponse.fail("이미 처리된 코인 발행 요청입니다.");
        }

        //코인 발행량이 초과 됐는지 체크
        if (optionalEvent.get().getIssuedCoinCount() >= optionalEvent.get().getTotalCoinLimit()) {
            return BusinessResultResponse.fail("발행 코인 소진.");
        }

        //개인의 신청 코인 수량을 넘었는지도 체크
        userCoinLimit = optionalEvent.get().getUserCoinLimit();
        totalCoinLimit = optionalEvent.get().getTotalCoinLimit();
        issuedCoinCount = optionalEvent.get().getIssuedCoinCount();

        Member member = memberRepository.findByUserId(userId);
        int issuedCoin = coinIssuanceRepository.countByEventIdAndMemberId(eventId, member.getId());

        if (issuedCoin >= userCoinLimit) {
            return BusinessResultResponse.fail("개인 코인발행량 한도초과.");
        }


        return BusinessResultResponse.success("코인발행 가능");
    }

    @Transactional
    public void issueEventCoin(CoinIssueCommand command) {
        log.info("Issue event coin for member: {}, eventId: {}", command.getUserId(), command.getEventId());

        Integer currentIssuedSeq = 1;
        Integer userCoinLimit = 0;
        Integer totalCoinLimit = 0;
        Integer issuedCoinCount = 0;

        //이벤트 테이블에서 코인 정책 확인
        Optional<Event> optionalEvent = eventRepository.findByIdAndStatus(command.getEventId(), Constant.EventStatus.ACTIVE);

        //이벤트별 코인 최종 발행 갯수 조회
        Optional<CoinIssuance> lastCoinIssuanceOptional = coinIssuanceRepository.findFirstByEventIdOrderByIssuedSeqDesc(command.getEventId());

        //사용자 코인 발행 순번을 기록
        if (lastCoinIssuanceOptional.isPresent()) {
            currentIssuedSeq = lastCoinIssuanceOptional.get().getIssuedSeq() + currentIssuedSeq;
        }


        //코인발행 기록 테이블
        Member member = memberRepository.findByUserId(command.getUserId());

        CoinIssuance coinIssuance =  CoinIssuance.builder()
                                            .event(optionalEvent.get())
                                            .member(member)
                                            .issuedSeq(currentIssuedSeq)
                                            .issuanceKey(command.getTraceId())
                                            .build();

        coinIssuanceRepository.save(coinIssuance);

        //이벤트 코인 발행 갯수 업데이트
        optionalEvent.get().updateIssuedCoinCount(currentIssuedSeq);
        eventRepository.save(optionalEvent.get());

    }


    public CoinBalanceResponseDto getCoinBalance(CoinBalanceCommand command) {

        Member member = memberRepository.findByUserId(command.getUserId());
        int balance = (int) coinIssuanceRepository.countCoinBalanceByDynamicConditions(command.getEventId(), member.getId(), command.getStatus());

        return CoinBalanceResponseDto.builder().balance(balance).build();
    }

    public EventCoinStatResponseDto getEventCoinStat(EventCoinStatInquiryCommand command, Pageable pageable) {

        int remainCoin = eventRepository.getEventRemainCoinStat(command);
        Page<UserCoinBalanceStatResponseDto> userCoinBalanceStatResponseDtoList = coinIssuanceRepository.getUserCoinBalanceStat(command,pageable);

        return EventCoinStatResponseDto.builder().remainCoin(remainCoin).userCoinBalanceList(PageResponse.of(userCoinBalanceStatResponseDtoList)).build();
    }


}
