package com.kidari.event.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kidari.event.common.api.BusinessResultResponse;
import com.kidari.event.domain.apiTrace.service.ApiTraceLogService;
import com.kidari.event.domain.apiTrace.service.command.ApiTraceUpdateCommand;
import com.kidari.event.domain.coin.service.CoinService;
import com.kidari.event.domain.coin.service.command.CoinIssueCommand;
import com.kidari.event.domain.event.service.EventApplicationService;
import com.kidari.event.domain.event.service.command.RegistCouponEventCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventConsumer {

    private final ObjectMapper objectMapper;
    private final CoinService coinService;
    private final EventApplicationService eventApplicationService;
    private final ApiTraceLogService apiTraceLogService;

    @KafkaListener(topics = "coin-acquisition-topic", groupId = "kidari-event-group")
    public void consumeCoinRequest(String message) {

        CoinIssueCommand coinIssueCommand = null;

        try {
            // 1. 파싱 시도
            coinIssueCommand = objectMapper.readValue(message, CoinIssueCommand.class);
        } catch (Exception e) {
            //파싱조차 실패한 건은 재시도를 할 이유가 없기에 일단 성공처리하고 시스템 로그만 찍고 끝냄
            //다만 traceId 자체를 가져올수 없어 api로그업데이트는 생략
            log.error("메시지 파싱 실패: {}", message, e);
            return;
        }

        // 2. 비즈니스 로직 수행 (이미 issueCommand가 확보된 상태)
        try {
            log.info("Consume Coin issueCommand: {}", coinIssueCommand);
            BusinessResultResponse<Void> businessResultResponse = coinService.coinIssuable(coinIssueCommand.getEventId(),coinIssueCommand.getUserId(), coinIssueCommand.getTraceId());

            if(businessResultResponse.isSuccess()) {

                coinService.issueEventCoin(coinIssueCommand);

                updateTraceLog(coinIssueCommand.getTraceId(), HttpStatus.OK.value(), "코인발행완료","");

            } else {

                updateTraceLog(coinIssueCommand.getTraceId(), HttpStatus.OK.value(), businessResultResponse.getMessage(),"");

                return;
            }

        } catch (Exception e) {
            log.error("consumeCoinRequest 실패: traceId={}", coinIssueCommand.getTraceId(), e);
            updateTraceLog(coinIssueCommand.getTraceId(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "coupon-event-apply-topic", groupId = "kidari-event-group")
    public void consumeCouponRequest(String message) {

        RegistCouponEventCommand registCouponEventCommand = null;

        try {
            // 1. 파싱 시도
            registCouponEventCommand = objectMapper.readValue(message, RegistCouponEventCommand.class);
        } catch (Exception e) {
            //파싱조차 실패한 건은 재시도를 할 이유가 없기에 일단 성공처리하고 시스템 로그만 찍고 끝냄
            //다만 traceId 자체를 가져올수 없어 api로그업데이트는 생략
            log.error("메시지 파싱 실패: {}", message, e);
            return;
        }

        // 2. 비즈니스 로직 수행 (이미 couponEventApplyCommand 확보된 상태)
        try {
            log.info("Consume registCouponEventCommand: {}", registCouponEventCommand);
            BusinessResultResponse<Void> businessResultResponse = eventApplicationService.registCouponEvent(registCouponEventCommand);

            if(businessResultResponse.isSuccess()) {

                updateTraceLog(registCouponEventCommand.getTraceId(), HttpStatus.OK.value(), "응모완료","");

            } else {
                updateTraceLog(registCouponEventCommand.getTraceId(), HttpStatus.OK.value(), businessResultResponse.getMessage(),"");
                return;
            }

        } catch (Exception e) {
            log.error("consumeCouponRequest 실패: {}", message, e);
            updateTraceLog(registCouponEventCommand.getTraceId(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null,e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void updateTraceLog(String traceId, Integer statusCode, String responseData, String errorMessage) {
        ApiTraceUpdateCommand updateCommand = ApiTraceUpdateCommand.builder()
                .traceId(traceId)
                .statusCode(statusCode)
                .responseData(responseData)
                .errorMsg(errorMessage)
                .build();
        apiTraceLogService.updateApiTraceLog(updateCommand);
    }
}
