package com.achilles.cloud.zuul.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author zhangtao
 */
@Slf4j
@Component
public class CountRateLimit implements RateChecker {

    public static final String STRATEGY_TYPE = "count";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean acquire(String key, Long limit, Long interval) {
        final Long current = this.stringRedisTemplate.boundValueOps(key).increment(1L);
        Long expire = this.stringRedisTemplate.getExpire(key);
        if (expire == null || expire == -1) {
            this.stringRedisTemplate.expire(key, interval, SECONDS);
        }
        Long remaining = Math.max(-1, limit - current);
        log.info(Long.toString(remaining));
        if (remaining < 0) {
            return false;
        }
        return true;
    }

}
