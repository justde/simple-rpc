package org.justd.simple.provider;

import org.justd.simple.common.property.ZooKeeperProperties;
import org.justd.simple.provider.property.SimpleProviderProperties;
import org.justd.simple.provider.registry.ServiceRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhangjd
 * @title: ProviderConfiguration
 * @description:
 * @date 2020/1/414:38
 */

@Configuration
public class ProviderConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "simple.provider")
    public SimpleProviderProperties simpleProviderProperties(){
        return new SimpleProviderProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "zookeeper")
    public ZooKeeperProperties zooKeeperProperties(){
        return new ZooKeeperProperties();
    }

    @Bean
    public ServiceRegistry serviceRegistry(ZooKeeperProperties zooKeeperProperties){
        return new ServiceRegistry(zooKeeperProperties);
    }

    @Bean
    public ProviderInitializer simpleInitializer(SimpleProviderProperties simpleProviderProperties,
                                                 ServiceRegistry serviceRegistry){
        return new ProviderInitializer(simpleProviderProperties, serviceRegistry);
    }

}
