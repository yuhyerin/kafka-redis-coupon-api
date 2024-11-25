package com.kafka.redis.coupon.adapters.controller;

import com.kafka.redis.coupon.adapters.persistence.redis.TimeAttackVO;
import com.kafka.redis.coupon.application.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponIssueService couponIssueService;

    @PostMapping("/issue")
    public ResponseEntity<Boolean> issueCoupon(@RequestParam String couponCode, @RequestParam String userId, @RequestParam String dateTimeId) {
        TimeAttackVO vo = TimeAttackVO.builder()
                .couponCode(couponCode)
                .dateTimeId(dateTimeId)
                .userId(userId)
                .build();
        boolean issueCoupon = couponIssueService.issueCoupon(vo);
        return ResponseEntity.ok(issueCoupon);
    }

}
