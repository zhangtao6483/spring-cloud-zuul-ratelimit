package com.achilles.cloud.zuul.filter;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.achilles.cloud.zuul.Rate;
import com.achilles.cloud.zuul.config.LimitReq;
import com.achilles.cloud.zuul.config.RateLimitProperties;
import com.achilles.cloud.zuul.strategy.LimitReqStrategy;
import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * @author zhangtao
 * @date 2017/7/23.
 */
@Slf4j
@Component
public class RateLimitFilter extends ZuulFilter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private RateLimitProperties properties;

    private LimitReqStrategy limitReqStrategy;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return this.properties.isEnabled() && limitReq().isPresent();
    }

    @Override
    public Object run() {

        final RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletResponse response = ctx.getResponse();
        final HttpServletRequest request = ctx.getRequest();

        limitReq().ifPresent(limitReq -> {
            log.info("................");
            Rate rate = limitReqStrategy.consume(limitReq, getRemoteAddr(request));

            if (rate.getRemaining() < 0) {
                ctx.setResponseStatusCode(TOO_MANY_REQUESTS.value());
                ctx.put("rateLimitExceeded", "true");
                throw new ZuulRuntimeException(new ZuulException(TOO_MANY_REQUESTS.toString(),
                    TOO_MANY_REQUESTS.value(), null));
            }
        });

        return null;
    }

    private Optional<LimitReq> limitReq() {
        return (route() != null) ? Optional.ofNullable(this.properties.getLimitReqs().get(route().getId()))
            : Optional.empty();
    }

    private Route route() {
        String requestURI = URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
        return this.routeLocator.getMatchingRoute(requestURI);
    }

    private String getRemoteAddr(final HttpServletRequest request) {
        if (request.getHeader(X_FORWARDED_FOR) != null) {
            return request.getHeader(X_FORWARDED_FOR);
        }
        return request.getRemoteAddr();
    }

    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnMissingBean(RateLimiter.class)
    public static class RedisConfiguration {
        @Bean
        public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
            return new StringRedisTemplate(connectionFactory);
        }

        @Bean
        public LimitReqStrategy limitReqStrategy(RedisTemplate redisTemplate) {
            return new LimitReqStrategy(redisTemplate);
        }
    }

}
