package com.kidari.event.domain.event.repository;

import com.kidari.event.domain.entity.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventApplicationRepository extends JpaRepository<EventApplication, Long>, EventApplicationRepositoryCustom {

    @Query("select count(e) from EventApplication e where e.requestId = :requestId")
    long countByRequestId(String requestId);
}
