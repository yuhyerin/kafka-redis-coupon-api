package com.kafka.redis.coupon.adapters.persistence.jpa.read;

import com.kafka.redis.coupon.domain.model.Coupon;
import com.kafka.redis.coupon.domain.repository.CouponRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaReadCouponRepository extends JpaRepository<Coupon, Long>, CouponRepository {
    Optional<Coupon> findByCouponCode(String couponCode);
}
