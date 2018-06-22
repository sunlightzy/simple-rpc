package com.glmapper.simple.consumer;

import com.glmapper.simple.common.property.ZookeeperProperties;
import com.glmapper.simple.consumer.discovery.ServiceDiscovery;
import com.glmapper.simple.consumer.proxy.ConsumerProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author: Jerry
 */
@Configuration
public class ConsumerConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "zookeeper")
    public ZookeeperProperties zookeeperProperties() {
        return new ZookeeperProperties();
    }

    @Bean
    public ServiceDiscovery serviceDiscovery(ZookeeperProperties zookeeperProperties) {
        return new ServiceDiscovery(zookeeperProperties);
    }

    @Bean
    public ConsumerProxy consumerProxy(ServiceDiscovery serviceDiscovery) {
        return new ConsumerProxy(serviceDiscovery);
    }

}
