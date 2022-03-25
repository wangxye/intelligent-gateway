package com.wxy.gateway.listener;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.wxy.gateway.config.ClientConfigProperties;
import com.wxy.gateway.constants.AdminConstants;
import com.wxy.gateway.constants.NacosConstants;
import com.wxy.gateway.exception.IntegateException;
import com.wxy.gateway.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 1. 将服务信息注册到Nacos注册中心
 * 2. 通知admin服务上线了并注册下线hook。
 */
public class AutoRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AutoRegisterListener.class);

    private volatile AtomicBoolean registered = new AtomicBoolean(false);

    private final ClientConfigProperties properties;

    private final ExecutorService pool;

    @NacosInjected
    private NamingService namingService;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    /**
     * url list to ignore
     */
    private static List<String> ignoreUrlList = new LinkedList<>();

    static {
        ignoreUrlList.add("/error");
    }

    public AutoRegisterListener(ClientConfigProperties properties) {
        if (!check(properties)) {
            LOGGER.error("client config port,contextPath,appName adminUrl and version can't be empty!");
            throw new IntegateException("client config port,contextPath,appName adminUrl and version can't be empty!");
        }
        this.properties = properties;
        pool = new ThreadPoolExecutor(1, 4, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    private boolean check(ClientConfigProperties properties) {
        if (properties.getPort() == null || properties.getContetxPath() == null
                || properties.getVersion() == null || properties.getAppName() == null
                || properties.getAdminUrl() == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!registered.compareAndSet(false, true)) {
            return;
        }

        doRegister();
        registerShutDownHook();
    }

    /**
     * 通知admin服务上线了并注册下线hook。
     */
    private void registerShutDownHook() {

    }

    /**
     * 将服务信息注册到Nacos注册中心
     */
    private void doRegister() {
        Instance instance = new Instance();
        instance.setIp(IpUtil.getLocalIpAddress());
        instance.setPort(properties.getPort());
        instance.setEphemeral(true);
        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("version", properties.getVersion());
        metadataMap.put("appName", properties.getAppName());
        instance.setMetadata(metadataMap);
        try {
            namingService.registerInstance(properties.getAppName(), NacosConstants.APP_GROUP_NAME, instance);
        } catch (NacosException e) {
            LOGGER.error("register to nacos fail", e);
            throw new IntegateException(e.getErrCode(), e.getErrMsg());
        }

        LOGGER.info("register interface info to nacos success!");
        // TODO send register request to ship-admin
        String url = "http://" + properties.getAdminUrl() + AdminConstants.REGISTER_PATH;
//        RegisterAppDTO registerAppDTO = buildRegisterAppDTO(instance);
//        OkhttpTool.doPost(url, registerAppDTO);
        LOGGER.info("register to ship-admin success!");
    }
}
