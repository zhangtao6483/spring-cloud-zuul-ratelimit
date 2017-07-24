package com.achilles.cloud.zuul.strategy;

import com.achilles.cloud.zuul.Rate;
import com.achilles.cloud.zuul.config.LimitReq;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author zhangtao
 * @date 2017/7/24.
 */
public class LimitReqStrategy {

    private RedisTemplate template;

    public LimitReqStrategy(final RedisTemplate template) {
        Assert.notNull(template, "RedisTemplate cannot be null");
        this.template = template;
    }

    public Rate consume(LimitReq limitReq, String key) {
        Long rate = limitReq.getRate();
        final Long current = this.template.boundValueOps(key).increment(1L);
        Long expire = this.template.getExpire(key);
        if (expire == null || expire == -1) {
            this.template.expire(key, 1, SECONDS);
            expire = 1L;
        }

        return new Rate(rate, Math.max(-1, rate - current), SECONDS.toMillis(expire), null);
    }
}
