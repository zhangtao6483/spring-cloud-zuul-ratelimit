package com.achilles.cloud.zuul.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author zhangtao
 */
@Slf4j
public class CountRateChecker implements RateChecker {

    public static final String STRATEGY_TYPE = "count";

    private final StringRedisTemplate stringRedisTemplate;

    public CountRateChecker(final StringRedisTemplate stringRedisTemplate) {
        Assert.notNull(stringRedisTemplate, "RedisTemplate cannot be null");
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean acquire(final String key, final Long limit, final Long interval) {
        final Long current = this.stringRedisTemplate.boundValueOps(key).increment(1L);
        final Long expire = this.stringRedisTemplate.getExpire(key);
        if (expire == null || expire == -1) {
            this.stringRedisTemplate.expire(key, interval, SECONDS);
        }
        final Long remaining = Math.max(-1, limit - current);
        log.info(Long.toString(remaining));
        if (remaining < 0) {
            return false;
        }
        return true;
    }

}
