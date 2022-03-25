package com.wxy.gateway.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 1. 将服务信息注册到Nacos注册中心
 * 2. 通知admin服务上线了并注册下线hook。
 */
public class AutoRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

    }
}
