package com.glmapper.simple.consumer.proxy;

import com.glmapper.simple.common.SimpleRequest;
import com.glmapper.simple.common.SimpleResponse;
import com.glmapper.simple.consumer.SimpleConsumer;
import com.glmapper.simple.consumer.discovery.ServiceDiscovery;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 客户端RPC调用代理
 *
 * @author Jerry
 */
public class ConsumerProxy {

    private ServiceDiscovery serviceDiscovery;

    public ConsumerProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new RpcInvocationHandler());
    }

    private class RpcInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 创建并初始化 RPC 请求
            SimpleRequest request = new SimpleRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            String serverAddress = null;
            if (serviceDiscovery != null) {
                // 发现服务
                serverAddress = serviceDiscovery.discover();
            }
            if (null == serverAddress) {
                throw new Exception("no server address available");
            }
            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            // 初始化 RPC 客户端
            SimpleConsumer client = new SimpleConsumer(host, port);
            // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
            SimpleResponse response = client.send(request);

            if (response.getError() != null) {
                throw response.getError();
            } else {
                return response.getResult();
            }
        }
    }
}
