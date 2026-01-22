package com.kidari.event.domain.port.out;

public interface EventPort {
    void sendCoinAcquisitionRequest(CoinAcquisitionEvent event);
    void sendCouponEventApplyRequest(CouponApplyEvent event);
}
