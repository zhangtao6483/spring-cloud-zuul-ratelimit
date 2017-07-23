package com.achilles.cloud.zuul.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

/**
 * @author zhangtao
 * @date 2017/7/23.
 */
public class RedisRateLimiter {
    private final RedisTemplate template;

    public RedisRateLimiter(final RedisTemplate template) {
        Assert.notNull(template, "RedisTemplate cannot be null");
        this.template = template;
    }

}
