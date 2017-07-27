package com.achilles.cloud.zuul.strategy;

import com.achilles.cloud.zuul.Rate;
import com.achilles.cloud.zuul.config.Policies;
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
public class CountRateLimit implements RateLimit {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean remaining(Policies policies, String key) {
        Rate rate = consume(policies, key);
        log.info(rate.toString());
        if (rate.getRemaining() < 0) {
            return false;
        }
        return true;
    }

    public Rate consume(Policies policies, String key) {

        final Long limit = policies.getLimit();
        final Long refreshInterval = policies.getRefreshInterval();
        final Long current = this.stringRedisTemplate.boundValueOps(key).increment(1L);
        Long expire = this.stringRedisTemplate.getExpire(key);
        if (expire == null || expire == -1) {
            this.stringRedisTemplate.expire(key, refreshInterval, SECONDS);
            expire = refreshInterval;
        }
        return new Rate(limit, Math.max(-1, limit - current), SECONDS.toMillis(expire), null);
    }

}
