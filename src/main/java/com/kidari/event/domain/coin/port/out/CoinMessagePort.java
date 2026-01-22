package com.kidari.event.domain.coin.port.out;

public interface CoinMessagePort {
    void sendCoinAcquisitionRequest(CoinAcquisitionEvent event);
}
