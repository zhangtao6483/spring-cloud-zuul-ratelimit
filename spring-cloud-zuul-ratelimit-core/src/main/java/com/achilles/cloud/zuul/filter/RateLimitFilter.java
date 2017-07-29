package com.achilles.cloud.zuul.filter;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.achilles.cloud.zuul.config.Policies;
import com.achilles.cloud.zuul.config.Policies.Type;
import com.achilles.cloud.zuul.config.RateLimitProperties;
import com.achilles.cloud.zuul.exception.CustomZuulRuntimeException;
import com.achilles.cloud.zuul.strategy.CountRateLimit;
import com.achilles.cloud.zuul.strategy.RateChecker;
import com.achilles.cloud.zuul.strategy.TokenBucketRateLimit;
import com.achilles.cloud.zuul.util.SpringUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import static com.achilles.cloud.zuul.config.Policies.Type.ORIGIN;
import static com.achilles.cloud.zuul.config.Policies.Type.URL;
import static com.achilles.cloud.zuul.config.Policies.Type.USER;
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
	private static final String ANONYMOUS = "anonymous";

	@Autowired
	private RouteLocator routeLocator;

	@Autowired
	private RateLimitProperties properties;

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		return this.properties.isEnabled() && policies().isPresent();
	}

	@Override
	public Object run() {

		final RequestContext ctx = RequestContext.getCurrentContext();
		final HttpServletResponse response = ctx.getResponse();
		final HttpServletRequest request = ctx.getRequest();

		policies().ifPresent(policies -> {
			Optional<RateChecker> rateChecker = selectStrategy(policies);
			rateChecker.ifPresent(r -> {
				if (!r.acquire(key(request, policies.getType()), policies.getLimit(), policies.getInterval())) {
					ctx.setResponseStatusCode(TOO_MANY_REQUESTS.value());
					ctx.put("rateLimitExceeded", "true");
					throw new CustomZuulRuntimeException(TOO_MANY_REQUESTS.toString(),
							TOO_MANY_REQUESTS.value(), null);
				}
			});
		});

		return null;
	}

	private Optional<RateChecker> selectStrategy(Policies policies) {
		if (StringUtils.equals(policies.getStrategy(), CountRateLimit.STRATEGY_TYPE)) {
			return Optional.of(SpringUtil.getBean(CountRateLimit.class));
		}
		if (StringUtils.equals(policies.getStrategy(), TokenBucketRateLimit.STRATEGY_TYPE)) {
			return Optional.of(SpringUtil.getBean(TokenBucketRateLimit.class));
		}
		return Optional.empty();
	}


	private Optional<Policies> policies() {
		return (route() != null) ? Optional.ofNullable(this.properties.getPolicies().get(route().getId()))
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

	private String key(final HttpServletRequest request, final List<Type> types) {
		final Route route = route();
		final StringJoiner joiner = new StringJoiner(":");
		joiner.add(route.getId());
		if (!types.isEmpty()) {
			if (types.contains(URL)) {
				joiner.add(route.getPath());
			}
			if (types.contains(ORIGIN)) {
				joiner.add(getRemoteAddr(request));
			}
			if (types.contains(USER)) {
				joiner.add(request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : ANONYMOUS);
			}
		}
		return joiner.toString();
	}

}
