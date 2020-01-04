package org.justd.simple.provider.service;

import org.justd.simple.api.HelloService;
import org.justd.simple.provider.annotation.SimpleProvider;

/**
 * @author Zhangjd
 * @title: HelloServiceImpl
 * @description:
 * @date 2020/1/320:13
 */
@SimpleProvider(HelloService.class)
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(String name) {
        return "Hello! "+ name;
    }
}
