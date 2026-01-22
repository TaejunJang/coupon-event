package com.kidari.event.domain.coin.repository;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.coin.entity.CoinIssuance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoinIssuanceRepository extends JpaRepository<CoinIssuance, Long>, CoinIssuanceRepositoryCustom {

    Optional<CoinIssuance> findFirstByEventIdOrderByIssuedSeqDesc(Long eventId);

    @Query("select count(c) from CoinIssuance c where c.event.id = :eventId and c.member.id = :memberId")
    int countByEventIdAndMemberId(@Param("eventId") Long eventId, @Param("memberId") Long memberId);

    @Query("select count(c) from CoinIssuance c where  c.member.id = :memberId and c.status = :status")
    int countByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") Constant.CoinStatus status);

    List<CoinIssuance> findByEventIdAndMemberIdAndStatus(Long eventId, Long memberId, Constant.CoinStatus status);

    boolean existsByIssuanceKey(String issuanceKey);
}
