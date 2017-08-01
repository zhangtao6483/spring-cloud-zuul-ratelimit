package com.achilles.cloud.zuul;

import com.achilles.cloud.zuul.config.RateLimitProperties;
import com.achilles.cloud.zuul.filter.RateLimitFilter;
import com.achilles.cloud.zuul.strategy.CountRateChecker;
import com.achilles.cloud.zuul.strategy.RateChecker;
import com.achilles.cloud.zuul.strategy.TokenBucketRateChecker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static com.achilles.cloud.zuul.config.RateLimitProperties.PREFIX;

/**
 * @author zhangtao
 * @date 2017/8/1.
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = "enabled", havingValue = "true")
public class RateLimitAutoConfiguration {

    @Bean
    public RateLimitFilter rateLimiterFilter(final RateChecker rateChecker,
                                             final RateLimitProperties rateLimitProperties,
                                             final RouteLocator routeLocator) {
        return new RateLimitFilter(rateChecker, rateLimitProperties, routeLocator);
    }

    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnMissingBean(RateChecker.class)
    @ConditionalOnProperty(prefix = PREFIX, name = "strategy", havingValue = CountRateChecker.STRATEGY_TYPE)
    public static class CountConfiguration {
        @Bean
        public StringRedisTemplate redisTemplate(final RedisConnectionFactory connectionFactory) {
            return new StringRedisTemplate(connectionFactory);
        }

        @Bean
        public RateChecker countRateChecker(final StringRedisTemplate stringRedisTemplate) {
            return new CountRateChecker(stringRedisTemplate);
        }
    }

    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnMissingBean(RateChecker.class)
    @ConditionalOnProperty(prefix = PREFIX, name = "strategy", havingValue = TokenBucketRateChecker.STRATEGY_TYPE)
    public static class TokenBucketConfiguration {
        @Bean
        public StringRedisTemplate redisTemplate(final RedisConnectionFactory connectionFactory) {
            return new StringRedisTemplate(connectionFactory);
        }

        @Bean
        public RateChecker tokenBucketRateChecker(final StringRedisTemplate stringRedisTemplate) {
            return new TokenBucketRateChecker(stringRedisTemplate);
        }
    }
}
