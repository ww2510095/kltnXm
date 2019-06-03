package com.example.fw.base.security;

import org.minbox.framework.api.boot.common.model.ApiBootResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
public class A {
    @GetMapping(value = "/test")
    public ApiBootResult testToken() {

        return ApiBootResult.builder().data("这是一个测试Token有效性的方法输出.").build();
    }
}
