package com.achilles.cloud.zuul.strategy;

import com.achilles.cloud.zuul.config.Policies;

public interface RateLimit {

    boolean remaining(Policies policies, String key);
}
