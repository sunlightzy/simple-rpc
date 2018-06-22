package com.glmapper.simple.provider.service;

import com.glmapper.simple.api.HelloService;
import com.glmapper.simple.provider.annotation.SimpleProvider;

/**
 * service implement class
 *
 * @author: Jerry
 * @date: 2018/6/22
 */
@SimpleProvider(HelloService.class)
public class HelloServiceImpl implements HelloService{

    /**
     * service function
     *
     * @param name
     * @return
     */
    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}
