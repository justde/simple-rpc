package org.justd.simple.consumer.proxy;

import lombok.extern.slf4j.Slf4j;
import org.justd.simple.common.SimpleException;
import org.justd.simple.common.SimpleRequest;
import org.justd.simple.common.SimpleResponse;
import org.justd.simple.consumer.discovery.ServiceDiscovery;
import org.justd.simple.consumer.handler.ConsumerHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Zhangjd
 * @title: ConsumerProxy
 * @description: 客户端RPC调用代理
 * @date 2020/1/415:21
 */
@Slf4j
public class ConsumerProxy {
    private volatile List<String> nodes = new ArrayList<>();

    private ServiceDiscovery serviceDiscovery;

    public ConsumerProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new SimpleInvocationHandler());
    }

    private class SimpleInvocationHandler implements InvocationHandler{

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws InterruptedException {
            SimpleRequest simpleRequest = buildRequest(method, args);
            String serverAddress = getServerAddress();
            String[] split = serverAddress.split(":");
            String host = "127.0.0.1";
            int port = Integer.parseInt(split[1]);
            ConsumerHandler consumerHandler = new ConsumerHandler(host, port);
            SimpleResponse response = consumerHandler.send(simpleRequest);
            if (response.getError() != null){
                throw new SimpleException("service invoker error,cause:", response.getError());
            }else {
                return response.getResult();
            }
        }

        private SimpleRequest buildRequest(Method method, Object[] args){
            SimpleRequest simpleRequest = new SimpleRequest();
            simpleRequest.setRequestId(UUID.randomUUID().toString());
            simpleRequest.setClassName(method.getDeclaringClass().getName());
            simpleRequest.setMethodName(method.getName());
            simpleRequest.setParameterTypes(method.getParameterTypes());
            simpleRequest.setParameters(args);
            return simpleRequest;
        }

        private String getServerAddress(){
            String serverAddress = null;
            if (serviceDiscovery != null){
                serverAddress = serviceDiscovery.discover();
            }
            if (null == serverAddress){
                throw new SimpleException("no server address available");
            }
            return serverAddress;
        }
    }
}
