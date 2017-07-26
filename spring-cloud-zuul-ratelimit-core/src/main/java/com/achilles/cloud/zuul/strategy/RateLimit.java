package com.achilles.cloud.zuul.strategy;

import com.achilles.cloud.zuul.Rate;
import com.achilles.cloud.zuul.config.LimitReq;

public interface RateLimit {

	Rate consume(LimitReq limitReq, String key);
}
