package com.kafka.redis.coupon.application.manager;

import com.kafka.redis.coupon.adapters.persistence.redis.TimeAttackVO;
import org.springframework.data.redis.core.RedisOperations;

import java.time.Duration;

public interface RedisOperation<T> {

    Long count(RedisOperations<String, Object> operations, T t);

    Long add(RedisOperations<String, Object> operations, T t);

    Boolean isMember(RedisOperations<String, Object> operations, TimeAttackVO vo);

    Long remove(RedisOperations<String, Object> operations, T t);

    Boolean delete(RedisOperations<String, Object> operations, T t);

    Boolean expire(RedisOperations<String, Object> operations, T t, Duration duration);

    String generateValue(T t);

    void execute(RedisOperations<String, Object> operations, T t);
}
