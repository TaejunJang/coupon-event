package com.kidari.event.domain.event.service;

import com.kidari.event.domain.coin.repository.CoinIssuanceRepository;
import com.kidari.event.domain.event.repository.EventRepository;
import com.kidari.event.domain.member.repository.MemberRepository;
import com.kidari.event.domain.port.out.EventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventService {

    private final EventPort eventPort;
    private final CoinIssuanceRepository coinIssuanceRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

}
