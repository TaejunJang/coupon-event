package com.kidari.event.domain.event.service.dto;

import com.kidari.event.common.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEventApplicationResponseDto {

    private Long eventApplicationId;
    private String userId;
    private String eventTitle;
    private String couponName;
    private Constant.ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt;
    private LocalDateTime canceledAt;

}
