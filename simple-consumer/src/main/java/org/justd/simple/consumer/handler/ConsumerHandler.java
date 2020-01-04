package org.justd.simple.consumer.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.justd.simple.common.SimpleDecoder;
import org.justd.simple.common.SimpleEncoder;
import org.justd.simple.common.SimpleRequest;
import org.justd.simple.common.SimpleResponse;

import java.util.concurrent.CountDownLatch;

/**
 * @author Zhangjd
 * @title: ConsumerHandler
 * @description:
 * @date 2020/1/417:29
 */
@Slf4j
public class ConsumerHandler extends SimpleChannelInboundHandler<SimpleResponse> {

    private int port;
    private String host;
    private SimpleResponse response;

    private CountDownLatch latch = new CountDownLatch(1);

    public ConsumerHandler( String host, int port) {
        this.port = port;
        this.host = host;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SimpleResponse simpleResponse) throws Exception {
        response = simpleResponse;
        latch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client caught exception", cause);
        ctx.close();
    }

    public SimpleResponse send(SimpleRequest request) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            ChannelInitializer<SocketChannel> channelHandler = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                            .addLast(new SimpleEncoder(SimpleRequest.class))
                            .addLast(new SimpleDecoder(SimpleResponse.class))
                            .addLast(ConsumerHandler.this);
                }
            };
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(channelHandler)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();

            latch.await();
            if (response != null){
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }

    }


}
