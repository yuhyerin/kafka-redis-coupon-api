package com.kafka.redis.coupon.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 1. Redis에 키-값 저장
    public void setKey(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 2. Redis에서 키로 값 조회
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 3. Redis에서 키 삭제
    public boolean deleteKey(String key) {
        Boolean result = redisTemplate.delete(key);
        return result != null && result; // null 체크 후 반환
    }

}
