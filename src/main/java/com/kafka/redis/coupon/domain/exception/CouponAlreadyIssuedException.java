package com.kafka.redis.coupon.domain.exception;

// 쿠폰이 이미 발급된 경우 발생하는 예외
public class CouponAlreadyIssuedException extends RuntimeException {
    public CouponAlreadyIssuedException(String message) {
        super(message);
    }
}
