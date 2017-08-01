package com.achilles.cloud.zuul.strategy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author zhangtao
 */
@Slf4j
public class TokenBucketRateChecker implements RateChecker {

    public static final String STRATEGY_TYPE = "bucket";

    private final StringRedisTemplate stringRedisTemplate;

    public TokenBucketRateChecker(final StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean acquire(final String key, final Long limit, final Long interval) {
        final Boolean execute = this.stringRedisTemplate.execute(new Callback(key, limit, interval));
        if (execute == null) {
            return false;
        }
        return execute;
    }

    protected static class Callback implements SessionCallback<Boolean> {

        private final String key;

        private final Long limit;

        private final Long interval;

        private final String requestId;

        public Callback(final String key, final Long limit, final Long interval) {
            this.key = key;
            this.limit = limit;
            this.interval = interval;
            this.requestId = UUID.randomUUID().toString();
        }

        @Override
        public <K, V> Boolean execute(final RedisOperations<K, V> redisOperations) throws DataAccessException {
            return executeInternal((RedisOperations<String, String>)redisOperations);
        }

        private Boolean executeInternal(final RedisOperations<String, String> redisOperations) {
            redisOperations.multi();
            final long milliseconds = System.currentTimeMillis();
            final String callKey = this.requestId.concat("-").concat(Long.toString(milliseconds));

            redisOperations.opsForZSet().removeRangeByScore(this.key, Double.MIN_VALUE,
                milliseconds - TimeUnit.MILLISECONDS.convert(this.interval, SECONDS));
            redisOperations.opsForZSet().add(this.key, callKey, milliseconds);
            redisOperations.expire(this.key, this.interval, SECONDS);
            redisOperations.opsForZSet().count(this.key, Double.MIN_VALUE, Double.MAX_VALUE);

            final List<Object> result = redisOperations.exec();

            if (CollectionUtils.isEmpty(result)) {
                return false;
            }
            final Long count = (Long)result.get(3);

            if (count > this.limit) {
                redisOperations.opsForZSet().remove(this.key, callKey);
                return false;
            }

            return true;
        }
    }
}
