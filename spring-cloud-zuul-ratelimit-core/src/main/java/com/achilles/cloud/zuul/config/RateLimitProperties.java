package com.achilles.cloud.zuul.config;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.achilles.cloud.zuul.config.RateLimitProperties.PREFIX;

/**
 * @author zhangtao
 * @date 2017/7/23.
 */
@Data
@Configuration
@ConfigurationProperties(PREFIX)
public class RateLimitProperties {

    public static final String PREFIX = "zuul.ratelimit";

    private Map<String, LimitReq> limitReqMap = new LinkedHashMap<>();

    private boolean enabled;

    private boolean behindProxy;
}
