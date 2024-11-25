package com.kafka.redis.coupon.adapters.persistence.redis;

import com.kafka.redis.coupon.application.manager.RedisOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class TimeAttackOperation implements RedisOperation<TimeAttackVO> {

    @Override
    public Long count(RedisOperations<String, Object> operations, TimeAttackVO vo) {
        String key = vo.getKey();
        Long size = operations.opsForSet().size(key);
        log.info("[TimeAttackOperation] [count] key ::: {}, size ::: {}", key, size);
        return size;
    }

    @Override
    public Long add(RedisOperations<String, Object> operations, TimeAttackVO vo) {
        String key = vo.getKey();
        String value = this.generateValue(vo);
        Long result = operations.opsForSet().add(key, value);
        log.info(
                "[TimeAttackOperation] [add] key ::: {}, value ::: {}, result ::: {}", key, value, result);
        return result;
    }

    @Override
    public Boolean isMember(RedisOperations<String, Object> operations, TimeAttackVO vo) {
        String key = vo.getKey();
        String value = this.generateValue(vo);
        Boolean result = operations.opsForSet().isMember(key, value);
        log.info(
                "[TimeAttackOperation] [isMember] key ::: {}, value ::: {}, result ::: {}", key, value, result);
        return result;
    }

    @Override
    public Long remove(RedisOperations<String, Object> operations, TimeAttackVO vo) {
        String key = vo.getKey();
        String value = this.generateValue(vo);
        Long result = operations.opsForSet().remove(key, value);
        log.info(
                "[TimeAttackOperation] [remove] key ::: {}, value ::: {}, result ::: {}",
                key,
                value,
                result);
        return result;
    }

    @Override
    public Boolean delete(RedisOperations<String, Object> operations, TimeAttackVO vo) {
        String key = vo.getKey();
        Boolean result = operations.delete(key);
        log.info("[TimeAttackOperation] [delete] key ::: {}, result ::: {}", key, result);
        return result;
    }

    @Override
    public Boolean expire(
            RedisOperations<String, Object> operations, TimeAttackVO vo, Duration duration) {
        String key = vo.getKey();
        Boolean result = operations.expire(key, duration);
        log.info(
                "[TimeAttackOperation] [expire] key ::: {}, expire ::: {}, result ::: {}",
                key,
                duration,
                result);
        return result;
    }

    @Override
    public String generateValue(TimeAttackVO vo) {
        return String.valueOf(vo.getUserId());
    }

    @Override
    public void execute(RedisOperations<String, Object> operations, TimeAttackVO vo) {
        count(operations, vo);      // 1.수량 조회 [SCARD]
        isMember(operations, vo);   // 2.값 존재 확인 [SISMEMBER]
        add(operations, vo);        // 3.쿠폰 지급 [SADD]
    }
}