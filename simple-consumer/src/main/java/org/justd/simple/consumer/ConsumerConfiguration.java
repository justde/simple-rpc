package org.justd.simple.consumer;

import org.justd.simple.common.property.ZooKeeperProperties;
import org.justd.simple.consumer.discovery.ServiceDiscovery;
import org.justd.simple.consumer.proxy.ConsumerProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhangjd
 * @title: ConsumerConfiguration
 * @description:
 * @date 2020/1/417:29
 */
@Configuration
public class ConsumerConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "zookeeper")
    public ZooKeeperProperties zooKeeperProperties(){
        return new ZooKeeperProperties();
    }

    @Bean
    public ServiceDiscovery serviceDiscovery(ZooKeeperProperties zooKeeperProperties){
        return new ServiceDiscovery(zooKeeperProperties);
    }

    @Bean
    public ConsumerProxy consumerProxy(ServiceDiscovery serviceDiscovery){
        return  new ConsumerProxy(serviceDiscovery);
    }
}
