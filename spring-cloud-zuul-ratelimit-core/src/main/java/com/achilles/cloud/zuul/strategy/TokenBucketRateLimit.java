package com.achilles.cloud.zuul.strategy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author zhangtao
 */
@Slf4j
@Component
public class TokenBucketRateLimit implements RateChecker {

    public static final String STRATEGY_TYPE = "bucket";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean acquire(String key, Long limit, Long interval) {
        Boolean execute = stringRedisTemplate.execute(new Callback(key, limit, interval));
        if (execute == null) {
            return false;
        }
        return execute;
    }

    protected static class Callback implements SessionCallback<Boolean> {

        private String key;

        private Long limit;

        private Long interval;

        private String requestId;

        public Callback(String key, Long limit, Long interval) {
            this.key = key;
            this.limit = limit;
            this.interval = interval;
            this.requestId = UUID.randomUUID().toString();
        }

        @Override
        public <K, V> Boolean execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
            return executeInternal((RedisOperations<String, String>)redisOperations);
        }

        private Boolean executeInternal(final RedisOperations<String, String> redisOperations) {
            redisOperations.multi();
            long milliseconds = System.currentTimeMillis();
            String callKey = requestId.concat("-").concat(Long.toString(milliseconds));

            redisOperations.opsForZSet().removeRangeByScore(key, Double.MIN_VALUE,
                milliseconds - TimeUnit.MILLISECONDS.convert(interval, SECONDS));
            redisOperations.opsForZSet().add(key, callKey, milliseconds);
            redisOperations.expire(key, interval, SECONDS);
            redisOperations.opsForZSet().count(key, Double.MIN_VALUE, Double.MAX_VALUE);

            final List<Object> result = redisOperations.exec();

            if (CollectionUtils.isEmpty(result)) {
                return false;
            }
            final Long count = (Long)result.get(3);

            if (count > limit) {
                redisOperations.opsForZSet().remove(key, callKey);
                return false;
            }

            return true;
        }
    }
}
