package org.justd.simple.provider.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.justd.simple.common.SimpleRequest;
import org.justd.simple.common.SimpleResponse;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Zhangjd
 * @title: SimpleHandler
 * @description:
 * @date 2020/1/321:45
 */
@Slf4j
public class SimpleHandler extends SimpleChannelInboundHandler<SimpleRequest> {

    private final Map<String, Object> handlerMap;

    public SimpleHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SimpleRequest simpleRequest) throws Exception {
        SimpleResponse response = new SimpleResponse();
        response.setRequestId(simpleRequest.getRequestId());
        System.out.println("2333333");
        Object handle = handle(simpleRequest);
        response.setResult(handle);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(SimpleRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        System.out.println("sssssss");
        Class<?> aClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        FastClass fastClass = FastClass.create(aClass);
        FastMethod method = fastClass.getMethod(methodName, parameterTypes);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception", cause);
        ctx.close();
    }
}
