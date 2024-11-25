package com.kafka.redis.coupon.adapters.persistence.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeAttackVO {
    private String key;
    private String couponCode;
    private String dateTimeId;
    private String userId;

    public String getKey() {
        return String.format("coupon:time-attack:%s:date-time:%s:issued:users", couponCode, dateTimeId);
    }
}
