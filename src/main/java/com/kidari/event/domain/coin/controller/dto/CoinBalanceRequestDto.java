package com.kidari.event.domain.coin.controller.dto;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.coin.service.command.CoinBalanceCommand;
import com.kidari.event.domain.coin.service.command.CoinIssuanceCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinBalanceRequestDto {
    @NotBlank(message = "요청 유저 아이디 누락")
    private String userId;

    private Long eventId;

    private Constant.CoinStatus status;

    public CoinBalanceCommand toCommand() {
        return CoinBalanceCommand.builder()
                .userId(this.userId)
                .eventId(this.eventId)
                .status(this.status)
                .build();
    }
}
