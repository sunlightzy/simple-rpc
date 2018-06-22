package com.glmapper.simple.consumer;

import com.glmapper.simple.api.HelloService;
import com.glmapper.simple.consumer.proxy.ConsumerProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 启动类
 *
 * @author: Jerry
 */
@SpringBootApplication
public class ConsumerBootstrap {
    private static final List<String> CHARACTERS = new ArrayList<>();

    static {
        CHARACTERS.add("Jerry");
        CHARACTERS.add("Judy");
        CHARACTERS.add("Tom");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ConsumerBootstrap.class, args);
        String[] names = applicationContext.getBeanDefinitionNames();
        CHARACTERS.addAll(Arrays.asList(names));
        ConsumerProxy proxy = applicationContext.getBean(ConsumerProxy.class);
        HelloService helloService = proxy.create(HelloService.class);
        while (true) {
            try {
                Thread.sleep(3000);
                String ack = CHARACTERS.get(ThreadLocalRandom.current().nextInt(CHARACTERS.size()));
                String echo = helloService.hello(ack);
                System.out.println(echo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
