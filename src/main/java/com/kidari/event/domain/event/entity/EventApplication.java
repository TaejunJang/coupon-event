package com.kidari.event.domain.event.entity;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.coin.entity.CoinIssuance;
import com.kidari.event.domain.coupon.entity.Coupon;
import com.kidari.event.domain.entity.BaseTimeEntity;
import com.kidari.event.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_event_applications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class EventApplication extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CoinIssuance coinIssuance;

    @Enumerated(EnumType.STRING)
    private Constant.ApplicationStatus status;

    private String requestId; // 멱등성 보장 키

    private LocalDateTime appliedAt;

    private LocalDateTime canceledAt;

    @Builder
    public EventApplication(Event event, Member member, Coupon coupon, CoinIssuance coinIssuance, String requestId) {
        this.event = event;
        this.member = member;
        this.coupon = coupon;
        this.coinIssuance = coinIssuance;
        this.status = Constant.ApplicationStatus.APPLIED;
        this.requestId = requestId;
        this.appliedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = Constant.ApplicationStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

}
