package com.achilles.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangtao
 * @date 2017/7/21.
 */
@EnableZuulProxy
@SpringCloudApplication
public class NonsenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(NonsenseApplication.class, args);
    }

    @RestController
    @RequestMapping("/services")
    public class ServiceController {

        public static final String RESPONSE_BODY = "OK";

        @GetMapping("/nonsense")
        public ResponseEntity<String> nonsense() {
            return ResponseEntity.ok(RESPONSE_BODY);
        }

    }
}
