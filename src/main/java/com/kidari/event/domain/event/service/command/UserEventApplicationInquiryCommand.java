package com.kidari.event.domain.event.service.command;

import com.kidari.event.common.Constant;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserEventApplicationInquiryCommand {

    private final String userId;
    private final Constant.ApplicationStatus applicationStatus;

}
