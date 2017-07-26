package com.achilles.cloud.zuul.strategy;

import com.achilles.cloud.zuul.Rate;
import com.achilles.cloud.zuul.config.LimitReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class CountRateLimit implements RateLimit{

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public Rate consume(LimitReq limitReq, String key) {
		Long rate = limitReq.getRate();
		final Long current = this.stringRedisTemplate.boundValueOps(key).increment(1L);
		Long expire = this.stringRedisTemplate.getExpire(key);
		if (expire == null || expire == -1) {
			this.stringRedisTemplate.expire(key, 10, SECONDS);
			expire = 1L;
		}

		return new Rate(rate, Math.max(-1, rate - current), SECONDS.toMillis(expire), null);
	}
}
