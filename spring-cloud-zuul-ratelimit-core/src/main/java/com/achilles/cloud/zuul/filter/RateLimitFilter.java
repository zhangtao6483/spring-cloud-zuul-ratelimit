package com.achilles.cloud.zuul.filter;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.achilles.cloud.zuul.config.LimitReq;
import com.achilles.cloud.zuul.config.RateLimitProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

/**
 * @author zhangtao
 * @date 2017/7/23.
 */
@Slf4j
@Component
public class RateLimitFilter extends ZuulFilter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    private RouteLocator routeLocator;

    @Autowired
    private RateLimitProperties properties;

    @Override
    public String filterType() {
        return null;
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
        log.info("................");

        return null;
    }

    private Optional<LimitReq> limitReq() {
        return (route() != null) ? Optional.ofNullable(this.properties.getLimitReqMap().get(route().getId()))
            : Optional.empty();
    }

    private Route route() {
        String requestURI = URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
        return this.routeLocator.getMatchingRoute(requestURI);
    }

    private String getRemoteAddr(final HttpServletRequest request) {
        if (this.properties.isBehindProxy() && request.getHeader(X_FORWARDED_FOR) != null) {
            return request.getHeader(X_FORWARDED_FOR);
        }
        return request.getRemoteAddr();
    }

}
