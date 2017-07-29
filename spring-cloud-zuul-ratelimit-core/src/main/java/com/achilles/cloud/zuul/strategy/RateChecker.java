package com.achilles.cloud.zuul.strategy;

public interface RateChecker {

	boolean acquire(String key, Long limit, Long interval);
}
