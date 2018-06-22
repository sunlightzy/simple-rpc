package com.glmapper.simple.common.util;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public final class ZookeeperUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperUtils.class);

    public static ZooKeeper connectServer(String registryAddress, int timeout) {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = null;
        try {
            Watcher watcher = event -> {
                // 判断是否已连接ZK,连接后计数器递减.
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            };
            zk = new ZooKeeper(registryAddress, timeout, watcher);
            // 若计数器不为0,则等待.
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("get zookeeper connect error, cause:", e);
        }
        return zk;
    }
}
