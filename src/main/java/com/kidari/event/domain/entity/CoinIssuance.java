package com.kidari.event.domain.entity;

import com.kidari.event.common.Constant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "tb_coin_issuances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@BatchSize(size = 100)
@DynamicUpdate
public class CoinIssuance extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;


    private Integer issuedSeq;

    private String issuanceKey;

    @Enumerated(EnumType.STRING)
    private Constant.CoinStatus status = Constant.CoinStatus.AVAILABLE;


    @Builder
    public CoinIssuance(Event event, Member member, Integer issuedSeq, String issuanceKey) {
        this.event = event;
        this.member = member;
        this.issuedSeq = issuedSeq;
        this.issuanceKey = issuanceKey;
    }

    public void use() {
        this.status = Constant.CoinStatus.USED;
    }

    public void release() {
        this.status = Constant.CoinStatus.AVAILABLE;
    }

}
