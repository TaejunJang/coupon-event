package com.kidari.event.domain.event.controller.dto;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.event.service.command.UserEventApplicationInquiryCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventApplicationSearchConditionDto {

    @NotBlank(message = "조회 요청 유저아이디 누락")
    private String userId;

    private Constant.ApplicationStatus applicationStatus;

    public UserEventApplicationInquiryCommand toCommand() {
       return UserEventApplicationInquiryCommand.builder()
                                                .userId(this.userId)
                                                .applicationStatus(this.applicationStatus)
                                                .build();
    }

}
