package com.kafka.redis.coupon.adapters.kafka;

import com.kafka.redis.coupon.adapters.persistence.redis.TimeAttackVO;
import com.kafka.redis.coupon.application.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssuedEventConsumer {
    private final CouponIssueService couponIssueService;

    @KafkaListener(topics = "TimeAttackCouponIssue", groupId = "coupon-service-group")
    public void couponIssueComplete(String message) {
        log.info("Received Coupon Issued Complete :: message : {}", message);
        // TODO DB Write 작업
    }

    @KafkaListener(topics = "CouponIssuedTopic", groupId = "coupon-service-group")
    public void consume(String message) {
        log.info("Received Coupon Issued Event: {}", message);

        // 수신한 Kafka 메시지에서 쿠폰 정보 파싱 (예: JSON -> DTO 변환)
        Long userId = parseUserIdFromMessage(message);
        String couponCode = parseCouponCodeFromMessage(message);

        TimeAttackVO vo = new TimeAttackVO();
        vo.setCouponCode(couponCode);
        vo.setDateTimeId("1");
        vo.setUserId(userId+"");

        // 비즈니스 로직 실행: 수신된 쿠폰 이벤트 처리
        couponIssueService.issueCoupon(vo);
    }

    private Long parseUserIdFromMessage(String message) {
        // 메시지 파싱 로직 (예: JSON 파싱)
        return 12345L; // 예시 데이터
    }

    private String parseCouponCodeFromMessage(String message) {
        // 메시지 파싱 로직 (예: JSON 파싱)
        return "COUPON123"; // 예시 데이터
    }
}
