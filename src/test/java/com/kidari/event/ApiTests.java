package com.kidari.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kidari.event.domain.coin.controller.dto.CoinIssuanceRequestDto;
import com.kidari.event.domain.event.controller.dto.CouponEventApplyRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" },
        topics = { "coin-acquisition-topic", "coupon-event-apply-topic" })
@DirtiesContext
public class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private  ObjectMapper  objectMapper;


    // 테스트용 데이터 (data.sql에 있는 데이터 기반)
    private static final String USER_ID = "kdr001";
    private static final Long EVENT_ID = 1L; // 1분기 이벤트
    private static final Long COUPON_ID_1D = 1L; // 1분기 1일 휴가권
    private static final Long COUPON_ID_3D = 2L; // 1분기 1일 휴가권
    private static final Long EVENT_APPLICATION_ID = 1L; // 1분기 1일 휴가권

    @Test
    @Order(1)
    @DisplayName("1.응모 코인 획득 테스트")
    void requestEventCoinTest() throws Exception {
        CoinIssuanceRequestDto request = new CoinIssuanceRequestDto(USER_ID, EVENT_ID);

        //획득요청
        mockMvc.perform(post("/api/coins/acquisitions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("응모코인 신청 요청 완료"));

        // Kafka 비동기 처리 대기 (EmbeddedKafka 환경이라도 약간의 딜레이 필요할 수 있음)
        Thread.sleep(2000);

        //조회를 통한 완료처리
        mockMvc.perform(get("/api/coins/balances")
                       .param("userId", USER_ID)
                       .param("eventId", String.valueOf(EVENT_ID))
                       .param("status", "AVAILABLE"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.balance").value(1));

    }

    @Test
    @Order(2)
    @DisplayName("2. 사용자 응모 코인 수량 조회 테스트")
    void getCoinBalanceTest() throws Exception {

        mockMvc.perform(get("/api/coins/balances")
                        .param("userId", USER_ID)
                        .param("eventId", String.valueOf(EVENT_ID))
                        .param("status", "AVAILABLE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").exists());
    }

    @Test
    @Order(3)
    @DisplayName("3. 휴가 쿠폰 응모 테스트")
    void applyCouponEventTest() throws Exception {

        CoinIssuanceRequestDto coinRequest = new CoinIssuanceRequestDto(USER_ID, EVENT_ID);

        //획득요청
        mockMvc.perform(post("/api/coins/acquisitions")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(coinRequest)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("응모코인 신청 요청 완료"));

        // Kafka 비동기 처리 대기 (EmbeddedKafka 환경이라도 약간의 딜레이 필요할 수 있음)
        Thread.sleep(2000);


        CouponEventApplyRequestDto applyRequest = new CouponEventApplyRequestDto(USER_ID, EVENT_ID, COUPON_ID_1D);

        //쿠폰 응모요청
        mockMvc.perform(post("/api/events/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("쿠폰응모 신청 요청 완료"));

        // Kafka 비동기 처리 대기 (EmbeddedKafka 환경이라도 약간의 딜레이 필요할 수 있음)
        Thread.sleep(5000);


        //요청 조회 확인
        mockMvc.perform(get("/api/events/applications")
                       .param("userId", USER_ID)
                       .param("page", "0")
                       .param("size", "20"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.content").isNotEmpty());

    }

    @Test
    @Order(4)
    @DisplayName("4. 사용자 응모 현황 조회 테스트")
    void getMyEventApplicationsTest() throws Exception {
        mockMvc.perform(get("/api/events/applications")
                        .param("userId", USER_ID)
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("5. 휴가 쿠폰별 전체 응모 현황 조회 테스트")
    void getCouponStatsTest() throws Exception {
        mockMvc.perform(get("/api/coupons/events/stats")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }
    
    @Test
    @Order(6)
    @DisplayName("6. 전체 응모 코인 현황 조회 테스트")
    void getCoinStatsTest() throws Exception {
        mockMvc.perform(get("/api/coins/stats")
                        .param("eventId", String.valueOf(EVENT_ID))
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userCoinBalanceList").exists());
    }

    @Test
    @Order(7)
    @DisplayName("7. 휴가 쿠폰 응모 취소 테스트")
    void cancelCouponEventTest() throws Exception {
        //1. 코인획득 요청
        CoinIssuanceRequestDto coinRequest = new CoinIssuanceRequestDto(USER_ID, EVENT_ID);
        mockMvc.perform(post("/api/coins/acquisitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coinRequest)));

        // Kafka 비동기 처리 대기 (Testcontainers/EmbeddedKafka 환경이라도 약간의 딜레이 필요할 수 있음)
        Thread.sleep(2000);

        // 2. 쿠폰 응모
        CouponEventApplyRequestDto applyRequest = new CouponEventApplyRequestDto(USER_ID, EVENT_ID, COUPON_ID_1D);
        mockMvc.perform(post("/api/events/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applyRequest)));

        Thread.sleep(2000);


        // 4. 취소 요청
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/events/coupons/" + EVENT_APPLICATION_ID + "/cancel"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("쿠몬응모 취소완료"));
    }
}