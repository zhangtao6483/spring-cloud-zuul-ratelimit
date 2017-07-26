package com.achilles.cloud.zuul;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zhangtao
 * @date 2017/7/24.
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Rate {

    private Long limit;
    private Long remaining;
    private Long reset;
    private Date expiration;

}