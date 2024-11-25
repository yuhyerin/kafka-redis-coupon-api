package com.kafka.redis.coupon.adapters.controller;

import com.kafka.redis.coupon.application.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    // 1. Redis에 키-값 저장 API
    @PostMapping("/set")
    public String setKeyValue(@RequestParam String key, @RequestParam String value) {
        redisService.setKey(key, value);
        return "Key-Value pair saved: {" + key + ": " + value + "}";
    }

    // 2. Redis에서 키 조회 API
    @GetMapping("/get")
    public String getValue(@RequestParam String key) {
        String value = redisService.getValue(key);
        return value != null ? "Value for key '" + key + "' is: " + value : "Key not found";
    }

    // 3. Redis 키 삭제 API
    @DeleteMapping("/delete")
    public String deleteKey(@RequestParam String key) {
        boolean isDeleted = redisService.deleteKey(key);
        return isDeleted ? "Key '" + key + "' deleted successfully." : "Key '" + key + "' not found or not deleted.";
    }
}
