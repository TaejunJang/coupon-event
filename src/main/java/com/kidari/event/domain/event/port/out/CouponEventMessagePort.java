package com.kidari.event.domain.event.port.out;

public interface CouponEventMessagePort {
    void sendCouponEventApplyRequest(CouponApplyEvent event);
}
