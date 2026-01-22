package com.kidari.event.domain.coin.controller.dto;

import com.kidari.event.domain.coin.service.command.CoinIssuanceCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinIssuanceRequestDto {
    @NotBlank(message = "요청 유저 아이디 누락")
    private String userId;
    
    @NotNull(message = "요청 이벤트 아이디 누락")
    private Long eventId;

    public CoinIssuanceCommand toCommand() {
        return CoinIssuanceCommand.builder()
                .userId(this.userId)
                .eventId(this.eventId)
                .build();
    }
}
