package org.justd.simple.consumer;

import lombok.extern.slf4j.Slf4j;
import org.justd.simple.api.HelloService;
import org.justd.simple.consumer.proxy.ConsumerProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zhangjd
 * @title: ConsumerBootstrap
 * @description:
 * @date 2020/1/415:14
 */
@SpringBootApplication
@Slf4j
public class ConsumerBootstrap {
    private static final List<String> CHARACTERS = new ArrayList<>();

    static {
        CHARACTERS.add("Jerry");
        CHARACTERS.add("Judy");
        CHARACTERS.add("Tom");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ConsumerBootstrap.class, args);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        CHARACTERS.addAll(Arrays.asList(beanDefinitionNames));
        ConsumerProxy proxy = applicationContext.getBean(ConsumerProxy.class);
        HelloService  helloService = proxy.create(HelloService.class);
        System.out.println(helloService.getClass().getName());
        while (true){
            try{
                Thread.sleep(3000);
                String ack = CHARACTERS.get(ThreadLocalRandom.current().nextInt(CHARACTERS.size()));
                String echo = helloService.hello(ack);
                log.info("result:{}", echo);
            } catch (InterruptedException e) {
                log.error("send error in main " + e);
                break;
            }
        }
    }
}
