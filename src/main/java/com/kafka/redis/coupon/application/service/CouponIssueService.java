package com.kafka.redis.coupon.application.service;

import com.kafka.redis.coupon.adapters.kafka.CouponEventProducer;
import com.kafka.redis.coupon.adapters.persistence.redis.RedisTransaction;
import com.kafka.redis.coupon.adapters.persistence.redis.TimeAttackOperation;
import com.kafka.redis.coupon.adapters.persistence.redis.TimeAttackVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponEventProducer couponEventProducer;
    private final RedisTemplate redisTemplate;
    private final RedisTransaction redisTransaction;
    private final TimeAttackOperation timeAttackOperation;
    private static final int maxIssueCount = 100; // 최대 발급 가능 쿠폰 수

    public boolean issueCoupon(TimeAttackVO vo) {
        // 1. Redis 트랜잭션 실행
        List<Object> results = redisTransaction.execute(redisTemplate, timeAttackOperation, vo);

        // 2. 결과 확인
        if (results != null && results.size() == 3) {
            Long currentStock = (Long) results.get(0);      // SCARD 결과
            Boolean isMember = (Boolean) results.get(1);    // SISMEMBER 결과
            Long issueResult = (Long) results.get(2);       // SADD 결과
            log.info("issueCoupon currentStock: {}, memberId: {}, isMember: {}, issueResult: {}",
                    currentStock, vo.getUserId(), isMember, issueResult);

            if (currentStock != null && currentStock >= maxIssueCount) {
                log.warn("쿠폰 수량 마감. 현재 재고: {}", currentStock);
                throw new IllegalStateException("쿠폰 수량이 마감되었습니다.");
            }

            if (isMember != null && isMember) {
                throw new IllegalStateException("사용자가 이미 쿠폰을 지급받았습니다.");
            }

            if (issueResult.equals(1L)) {
                // Kafka를 통해 발급 이벤트 발행
                couponEventProducer.sendCouponIssuedEvent(vo.getCouponCode(), vo.getUserId());
                return true;
            }
        }

        // 3. 예상치 못한 트랜잭션 결과 처리
        log.error("Redis 트랜잭션 결과가 예상과 다릅니다. results.size(): {}, results.toString(): {}"
                , results != null ? results.size() : "null"
                , results.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        return false;
    }
}
