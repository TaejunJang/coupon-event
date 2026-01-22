package com.kidari.event.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


public final class Constant {

    private Constant() {}

    /**
     * 이벤트 진행 상태
     */
    public enum EventStatus {
        READY,   // 준비 중
        ACTIVE,  // 진행 중
        ENDED    // 종료 및 발표 완료
    }

    /**
     * 개별 코인 상태
     */
    public enum CoinStatus {
        AVAILABLE, // 사용 가능 (대기)
        USED       // 사용 완료
    }

    /**
     * 쿠폰 응모 내역 상태
     */
    public enum ApplicationStatus {
        APPLIED,   // 응모 완료
        CANCELED,  // 응모 취소 (회수)
        WIN,       // 당첨
        LOSE       // 낙첨
    }

    /**
     * 쿠폰 타입
     */
    public enum CouponType {
        EVENT, // 이벤트쿠폰
        PRODCT // 주문상품쿠폰
    }


    /**
     * 쿠폰 인스턴스 상태
     */
    public enum CouponStatus {
        AVAILABLE, // 발급 가능
        CLOSED // 발급 불가
    }

}
