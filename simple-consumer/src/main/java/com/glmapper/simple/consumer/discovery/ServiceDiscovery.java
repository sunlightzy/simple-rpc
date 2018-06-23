package com.glmapper.simple.consumer.discovery;

import com.glmapper.simple.common.property.ZookeeperProperties;
import com.glmapper.simple.common.util.ZookeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 服务发现:连接ZK,添加watch事件
 *
 * @author Jerry
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private volatile List<String> nodes = new ArrayList<>();

    private ZookeeperProperties zookeeperProperties;

    public ServiceDiscovery(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
        String address = zookeeperProperties.getAddress();
        int timeout = zookeeperProperties.getTimeout();
        ZooKeeper zk = ZookeeperUtils.connectServer(address, timeout);
        if (zk != null) {
            watchNode(zk);
        }
    }

    public String discover() {
        String data = null;
        int size = nodes.size();
        if (size > 0) {
            if (size == 1) {
                data = nodes.get(0);
                LOGGER.debug("using only node: {}", data);
            } else {
                data = nodes.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random node: {}", data);
            }
        }
        return data;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            Watcher childrenNodeChangeWatcher = event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNode(zk);
                }
            };
            String rootPath = zookeeperProperties.getRootPath();
            List<String> nodeList = zk.getChildren(rootPath, childrenNodeChangeWatcher);
            List<String> nodes = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(rootPath + "/" + node, false, null);
                nodes.add(new String(bytes, Charset.forName("UTF-8")));
            }
            LOGGER.info("node data: {}", nodes);
            this.nodes = nodes;
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("节点监控出错，原因：", e);
        }
    }
}
