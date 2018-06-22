package com.glmapper.simple.consumer;

import com.glmapper.simple.common.SimpleDecoder;
import com.glmapper.simple.common.SimpleEncoder;
import com.glmapper.simple.common.SimpleRequest;
import com.glmapper.simple.common.SimpleResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * RPC真正调用客户端
 *
 * @author Jerry
 */
public class SimpleConsumer extends SimpleChannelInboundHandler<SimpleResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConsumer.class);

    private String host;

    private int port;

    private SimpleResponse response;

    private CountDownLatch latch = new CountDownLatch(1);

    private final Object obj = new Object();

    public SimpleConsumer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SimpleResponse response) throws Exception {
        this.response = response;
        latch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }

    public SimpleResponse send(SimpleRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelInitializer<SocketChannel> channelHandler = new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                            // 将 RPC 请求进行编码（为了发送请求）
                            .addLast(new SimpleEncoder(SimpleRequest.class))
                            // 将 RPC 响应进行解码（为了处理响应）
                            .addLast(new SimpleDecoder(SimpleResponse.class))
                            // 使用 RpcClient 发送 RPC 请求
                            .addLast(SimpleConsumer.this);
                }
            };
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(channelHandler)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();
            latch.await();
            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
}
