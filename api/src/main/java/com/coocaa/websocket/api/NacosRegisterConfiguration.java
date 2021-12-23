package com.coocaa.websocket.api;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import com.coocaa.websocket.api.util.IpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@EnableNacosDiscovery
public class NacosRegisterConfiguration {
    @NacosInjected
    NamingService namingService;

    @Value("${spring.application.name}")
    String applicationName;

    @Value("${project.http.port}")
    Integer serverPort;

    @PostConstruct
    public void registerInstance() throws NacosException {
        namingService.registerInstance(applicationName, IpUtil.getIp(), serverPort);
        List<Instance> allInstances = namingService.getAllInstances(applicationName);
        System.out.println(allInstances);
    }

}
