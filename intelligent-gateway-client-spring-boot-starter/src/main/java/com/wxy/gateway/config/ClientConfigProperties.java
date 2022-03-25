package com.wxy.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integate.http")
@Data
public class ClientConfigProperties {

    /**
     * 启动端口号
     */
    private Integer port;
    /**
     * 请求根路径
     */
    private String contetxPath;
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 接口版本号
     */
    private String version;

    private String adminUrl;
}
