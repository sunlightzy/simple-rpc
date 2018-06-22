package com.glmapper.simple.provider.registry;

import com.glmapper.simple.common.property.ZookeeperProperties;
import com.glmapper.simple.common.util.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * connect zookeeper to registry service
 *
 * @author Jerry
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private ZookeeperProperties zookeeperProperties;

    public ServiceRegistry(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    public void register(String data) {
        if (data != null) {
            ZooKeeper zk = ZookeeperUtils.connectServer(zookeeperProperties.getAddress(), zookeeperProperties.getTimeout());
            if (zk != null) {
                addRootNode(zk);
                createNode(zk, data);
            }
        }
    }

    /**
     * add one zookeeper root node
     *
     * @param zk
     */
    private void addRootNode(ZooKeeper zk) {
        try {
            String registryPath = zookeeperProperties.getRootPath();
            Stat s = zk.exists(registryPath, false);
            if (s == null) {
                zk.create(registryPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("zookeeper add root node error,cause:", e);
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
            String dataPath = zookeeperProperties.getRootPath() + zookeeperProperties.getDataPath();
            String path = zk.create(dataPath, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("create zookeeper node error,cause:", e);
        }
    }
}
