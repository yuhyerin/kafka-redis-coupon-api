package com.kafka.redis.coupon.adapters.persistence.redis;

import com.kafka.redis.coupon.application.manager.RedisOperation;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisTransaction {

    public List<Object> execute(
            RedisOperations<String, Object> redisTemplate, RedisOperation operation, Object vo) {

        return redisTemplate.execute(
                new SessionCallback<>() {
                    @Override
                    public List<Object> execute(RedisOperations callbackOperations) throws DataAccessException {

                        // [1] REDIS 트랜잭션 Start
                        callbackOperations.multi();

                        // [2] Operation 실행
                        operation.execute(callbackOperations, vo);

                        // [3] REDIS 트랜잭션 End
                        return callbackOperations.exec();
                    }
                });
    }
}
