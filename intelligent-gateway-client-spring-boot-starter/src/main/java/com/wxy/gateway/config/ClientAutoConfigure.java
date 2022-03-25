package com.wxy.gateway.config;


import com.wxy.gateway.listener.AutoRegisterListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {AutoRegisterListener.class})
@EnableConfigurationProperties(value = {ClientConfigProperties.class})
public class ClientAutoConfigure {

}
