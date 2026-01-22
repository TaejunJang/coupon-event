package com.kidari.event.domain.event.repository;

import com.kidari.event.domain.event.service.command.UserEventApplicationInquiryCommand;
import com.kidari.event.domain.event.service.dto.UserEventApplicationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventApplicationRepositoryCustom {

    Page<UserEventApplicationResponseDto> findUserApplicationHistory(UserEventApplicationInquiryCommand command, Pageable pageable);

}
