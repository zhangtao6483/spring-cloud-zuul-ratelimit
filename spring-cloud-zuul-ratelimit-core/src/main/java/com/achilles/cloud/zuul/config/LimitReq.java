package com.achilles.cloud.zuul.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangtao
 * @date 2017/7/23.
 */
@Data
@NoArgsConstructor
public class LimitReq {

    // connect count pre sec
    private Long rate;

}
