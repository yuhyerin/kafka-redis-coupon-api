package com.kafka.redis.coupon.adapters.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_NAME = "TimeAttackCouponIssue"; // Kafka Topic 이름

    /**
     * 쿠폰 발급 이벤트를 Kafka로 전송하는 메서드.
     *
     * @param couponCode 발급된 쿠폰 코드
     * @param userId     발급 대상 사용자 ID
     */
    public void sendCouponIssuedEvent(String couponCode, String userId) {
        String message = String.format("{\"userId\": %s, \"couponCode\": \"%s\"}", userId, couponCode);
        log.info("Sending Kafka message: {}", message);

        kafkaTemplate.send(TOPIC_NAME, message)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Successfully sent message to {}: {}", TOPIC_NAME, message);
                    } else {
                        log.error("Failed to send message to {}: {}", TOPIC_NAME, message, exception);
                    }
                });
    }

}
