package com.kafka.redis.coupon.adapters.persistence.jpa.write;

import com.kafka.redis.coupon.domain.model.Coupon;
import com.kafka.redis.coupon.domain.repository.WriteCouponRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWriteCouponRepository extends JpaRepository<Coupon, Long>, WriteCouponRepository {
}
