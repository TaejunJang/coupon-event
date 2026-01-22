package com.kidari.event.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kidari.event.domain.coin.port.out.CoinAcquisitionEvent;
import com.kidari.event.domain.coin.port.out.CoinMessagePort;
import com.kidari.event.domain.event.port.out.CouponApplyEvent;
import com.kidari.event.domain.event.port.out.CouponEventMessagePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventAdapter implements CoinMessagePort, CouponEventMessagePort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public void sendCoinAcquisitionRequest(CoinAcquisitionEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Produce Coin Request: {}", message);
            kafkaTemplate.send("coin-acquisition-topic", message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing CoinAcquisitionEvent", e);
        }
    }

    @Override
    public void sendCouponEventApplyRequest(CouponApplyEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Produce CouponEvent Request: {}", message);
            kafkaTemplate.send("coupon-event-apply-topic", message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing CouponApplyEvent", e);
        }
    }
}
