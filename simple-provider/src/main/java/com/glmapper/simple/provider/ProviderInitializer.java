package com.glmapper.simple.provider;


import com.glmapper.simple.common.*;
import com.glmapper.simple.provider.annotation.SimpleProvider;
import com.glmapper.simple.provider.handler.SimpleHandler;
import com.glmapper.simple.provider.property.SimpleProviderProperties;
import com.glmapper.simple.provider.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 启动并注册服务
 *
 * @author Jerry
 */
public class ProviderInitializer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderInitializer.class);

    private SimpleProviderProperties providerProperties;

    /**
     * service registry
     */
    private ServiceRegistry serviceRegistry;

    /**
     * store interface and service implement mapping
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public ProviderInitializer(SimpleProviderProperties providerProperties, ServiceRegistry serviceRegistry) {
        this.providerProperties = providerProperties;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 获取被 SimpleProvider 注解的 Bean
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(SimpleProvider.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(SimpleProvider.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelHandler channelHandler = new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                            .addLast(new SimpleDecoder(SimpleRequest.class))
                            .addLast(new SimpleEncoder(SimpleResponse.class))
                            .addLast(new SimpleHandler(handlerMap));
                }
            };
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelHandler)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String host = getLocalHost();
            if (null == host) {
                LOGGER.error("can't get service address,because address is null");
                throw new SimpleException("can't get service address,because address is null");
            }
            int port = providerProperties.getPort();
            ChannelFuture future = bootstrap.bind(host, port).sync();
            LOGGER.debug("server started on port {}", port);

            if (serviceRegistry != null) {
                String serverAddress = host + ":" + port;
                serviceRegistry.register(serverAddress);
            }
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * get service host
     *
     * @return
     */
    private String getLocalHost() {
        Enumeration<NetworkInterface> allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            LOGGER.error("get local address error,cause:", e);
            return null;
        }
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ip = addresses.nextElement();
                if (ip instanceof Inet4Address && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    return ip.getHostAddress();
                }
            }
        }
        return null;
    }
}
