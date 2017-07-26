package com.achilles.cloud.zuul.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangtao
 * @date 2017/7/26.
 */
@RestController
@RequestMapping("/error")
public class GlobalErrorController extends AbstractErrorController {

    public GlobalErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping()
    public ResponseEntity error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity(status);
    }
}
