package com.kidari.event.domain.coupon.repository;

import com.kidari.event.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long>,CouponRepositoryCustom {
}
