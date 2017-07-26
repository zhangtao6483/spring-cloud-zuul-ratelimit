package com.achilles.cloud.zuul.filter;

import javax.servlet.http.HttpServletResponse;

import com.achilles.cloud.zuul.exception.CustomZuulRuntimeException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * @author zhangtao
 * @date 2017/7/26.
 */
@Slf4j
@Component
public class ErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletResponse response = ctx.getResponse();
        Throwable throwable = ctx.getThrowable();
        log.error("[ErrorFilter] error message: {}", throwable.getCause().getMessage());
        ctx.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ctx.set("error.exception", throwable.getCause());

        if (throwable instanceof ZuulException) {
            ZuulException zuulException = (ZuulException)throwable;
            if (zuulException.getCause() instanceof CustomZuulRuntimeException) {
                CustomZuulRuntimeException e = (CustomZuulRuntimeException)zuulException.getCause();
                Integer statusCode = e.nStatusCode;
                if (statusCode == null) {
                    return null;
                }
                if (statusCode == TOO_MANY_REQUESTS.value()) {
                    ctx.setResponseBody("TOO MANY REQUEST");
                    ctx.getResponse().setContentType("application/json");
                    ctx.setResponseStatusCode(INTERNAL_SERVER_ERROR.value());
                }
            }
        }
        return null;
    }
}
