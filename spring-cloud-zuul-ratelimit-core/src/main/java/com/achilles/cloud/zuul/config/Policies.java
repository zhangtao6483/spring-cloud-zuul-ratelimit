package com.achilles.cloud.zuul.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author zhangtao
 * @date 2017/7/23.
 */
@Data
@NoArgsConstructor
public class Policies {

    private Long refreshInterval = MINUTES.toSeconds(1L);
    private Long limit;

    private List<Type> type = new ArrayList<>();

    public enum Type {
        ORIGIN,
        USER,
        URL
    }

}
