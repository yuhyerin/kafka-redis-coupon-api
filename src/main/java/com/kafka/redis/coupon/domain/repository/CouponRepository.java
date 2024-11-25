package com.kafka.redis.coupon.domain.repository;

import com.kafka.redis.coupon.domain.model.Coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByCouponCode(String couponCode);
}
