package com.glmapper.simple.provider;

import com.glmapper.simple.provider.property.SimpleProviderProperties;
import com.glmapper.simple.common.property.ZookeeperProperties;
import com.glmapper.simple.provider.registry.ServiceRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author: Jerry
 * @date: 2018/6/22
 */
@Configuration
public class ProviderConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "simple.provider")
    public SimpleProviderProperties simpleProviderProperties() {
        return new SimpleProviderProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "zookeeper")
    public ZookeeperProperties zookeeperProperties() {
        return new ZookeeperProperties();
    }

    @Bean
    public ServiceRegistry serviceRegistry(ZookeeperProperties zookeeperProperties) {
        return new ServiceRegistry(zookeeperProperties);
    }

    @Bean
    public ProviderInitializer simpleInitializer(SimpleProviderProperties simpleProviderProperties,
                                                 ServiceRegistry serviceRegistry) {
        return new ProviderInitializer(simpleProviderProperties, serviceRegistry);
    }
}
