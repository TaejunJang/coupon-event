package com.kidari.event.domain.event.repository;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>,EventRepositoryCustom {

    Optional<Event> findByIdAndStatus(Long eventId, Constant.EventStatus status);


}
