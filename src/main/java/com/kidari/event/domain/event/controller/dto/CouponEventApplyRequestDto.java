package com.kidari.event.domain.event.controller.dto;

import com.kidari.event.domain.event.service.command.CouponEventApplyCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponEventApplyRequestDto {

    @NotBlank(message = "요청 유저 아이디 누락")
    private String userId;

    @NotNull(message = "요청 이벤트 번호 누락")
    private Long eventId;

    @NotNull(message = "요청 쿠폰 번호 누락")
    private Long couponId;

    @Builder
    public CouponEventApplyCommand toCommand() {
        return CouponEventApplyCommand.builder()
                .userId(userId)
                .eventId(eventId)
                .couponId(couponId)
                .build();
    }

}
