package com.kafka.redis.coupon.domain.repository;

import com.kafka.redis.coupon.domain.model.Coupon;

public interface WriteCouponRepository {
    Coupon save(Coupon coupon);
}
