package org.justd.simple.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.justd.simple.common.*;
import org.justd.simple.provider.annotation.SimpleProvider;
import org.justd.simple.provider.handler.SimpleHandler;
import org.justd.simple.provider.property.SimpleProviderProperties;
import org.justd.simple.provider.registry.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhangjd
 * @title: ProviderInitializer
 * @description:
 * @date 2020/1/320:31
 */
@Slf4j
public class ProviderInitializer implements ApplicationContextAware, InitializingBean {

    private SimpleProviderProperties providerProperties;
    private ServiceRegistry registry;
    private Map<String, Object> handleMap = new HashMap<>();

    public ProviderInitializer(SimpleProviderProperties providerProperties, ServiceRegistry registry) {
        this.providerProperties = providerProperties;
        this.registry = registry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(SimpleProvider.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)){
            for (Object serviceBean : serviceBeanMap.values()){
                String interfaceName = serviceBean.getClass().getAnnotation(SimpleProvider.class).value().getName();
                handleMap.put(interfaceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelHandler channelHandler = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new SimpleDecoder(SimpleRequest.class))
                        .addLast(new SimpleEncoder(SimpleResponse.class))
                        .addLast(new SimpleHandler(handleMap));
            }
        };
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelHandler)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        String host = getLocalHost();
        if (host == null){
            log.error("can't get service address,because address is null");
            throw new SimpleException("can't get service address,because address is null");
        }
        int port = providerProperties.getPort();
        ChannelFuture future = bootstrap.bind(host, port).sync();
        log.debug("server started on port:{}",port);

        if (registry != null){
            String serverAddress = host + ":" + port;
            registry.register(serverAddress);
        }

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    private String getLocalHost(){
        Enumeration<NetworkInterface> allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            log.error("get local address error, cause:", e);
            return null;
        }
        while (allNetInterfaces.hasMoreElements()){
            NetworkInterface networkInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()){
                InetAddress ip = inetAddresses.nextElement();
                if (ip instanceof Inet4Address && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")){
                    return ip.getHostAddress();
                }
            }
        }
        return null;
    }
}
