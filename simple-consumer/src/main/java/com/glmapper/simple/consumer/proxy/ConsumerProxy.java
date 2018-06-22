package com.glmapper.simple.consumer.proxy;

import com.glmapper.simple.common.SimpleException;
import com.glmapper.simple.common.SimpleRequest;
import com.glmapper.simple.common.SimpleResponse;
import com.glmapper.simple.consumer.discovery.ServiceDiscovery;
import com.glmapper.simple.consumer.handler.ConsumerHandler;
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
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new SimpleInvocationHandler());
    }

    private class SimpleInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SimpleRequest request = buildRequest(method, args);
            String serverAddress = getServerAddress();
            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            ConsumerHandler consumerHandler = new ConsumerHandler(host, port);
            SimpleResponse response = consumerHandler.send(request);
            if (response.getError() != null) {
                throw new SimpleException("service invoker error,cause:", response.getError());
            } else {
                return response.getResult();
            }
        }

        private SimpleRequest buildRequest(Method method, Object[] args) {
            SimpleRequest request = new SimpleRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            return request;
        }

        private String getServerAddress() {
            String serverAddress = null;
            if (serviceDiscovery != null) {
                serverAddress = serviceDiscovery.discover();
            }
            if (null == serverAddress) {
                throw new SimpleException("no server address available");
            }
            return serverAddress;
        }
    }
}
