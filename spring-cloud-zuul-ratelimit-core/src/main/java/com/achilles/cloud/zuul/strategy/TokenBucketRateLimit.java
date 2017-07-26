package com.achilles.cloud.zuul.strategy;

import com.achilles.cloud.zuul.Rate;
import com.achilles.cloud.zuul.config.LimitReq;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenBucketRateLimit implements RateLimit {

	public Long timeStamp ;
	public Long capacity; // 桶的容量
	public Long rate; // 令牌放入速度
	public Long tokens; // 当前令牌数量

	@Bean
	public StringRedisTemplate redisTemplate() {
		StringRedisTemplate template = new StringRedisTemplate();
		// explicitly enable transaction support
		template.setEnableTransactionSupport(true);
		return template;
	}


	@Override
	public Rate consume(LimitReq limitReq, String key) {
		long now = new Date().getTime();
		// 先添加令牌
		tokens = Math.min(capacity, tokens + (now - timeStamp) * rate);
		timeStamp = now;


		return null;
	}
}
