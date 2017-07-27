package com.achilles.cloud.zuul.strategy;

import com.achilles.cloud.zuul.config.Policies;
import org.springframework.stereotype.Component;

/**
 * @author zhangtao
 */
@Component
public class TokenBucketRateLimit implements RateLimit {

    public Long timeStamp;
    public Long capacity; // 桶的容量
    public Long rate; // 令牌放入速度
    public Long tokens; // 当前令牌数量

    @Override
    public boolean remaining(Policies policies, String key) {
        return false;
    }

    public void consume(Policies policies, String key) {
        long now = System.currentTimeMillis();
        // 先添加令牌
        tokens = Math.min(capacity, tokens + (now - timeStamp) * rate);
        timeStamp = now;

    }

}
