package com.kidari.event.domain.entity;

import com.kidari.event.common.Constant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "tb_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Coupon extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Event event;

    @Column(name="coupon_type")
    @Enumerated(EnumType.STRING)
    private Constant.CouponType couponType;

    @Column(name="coupon_group_code")
    private String couponGroupCode;

    @Column(name = "coupon_name")
    private String couponName;

    @Column(name = "issue_limit")
    private String issueLimit;

    @Column(name = "issued_count")
    private String issuedCount;

    @Enumerated(EnumType.STRING)
    private Constant.CouponStatus status = Constant.CouponStatus.AVAILABLE;

    @Builder
    public Coupon(Event event, String couponName, String issueLimit) {
        this.event = event;
        this.couponName = couponName;
        this.issueLimit = issueLimit;
    }

}
