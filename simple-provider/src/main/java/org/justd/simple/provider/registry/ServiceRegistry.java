package org.justd.simple.provider.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.justd.simple.common.property.ZooKeeperProperties;
import org.justd.simple.common.util.ZookeeperUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Zhangjd
 * @title: ServiceRegistry
 * @description:
 * @date 2020/1/412:25
 */
@Slf4j
public class ServiceRegistry {

    private ZooKeeperProperties zooKeeperProperties;

    public ServiceRegistry(ZooKeeperProperties zooKeeperProperties) {
        this.zooKeeperProperties = zooKeeperProperties;
    }

    public void register(String data) {
        if (data != null) {
            ZooKeeper zooKeeper = ZookeeperUtils.connectServer(zooKeeperProperties.getAddress(), zooKeeperProperties.getTimeout());
            if (zooKeeper != null) {
                addRootNode(zooKeeper);
                createNode(zooKeeper, data);
            }
        }
    }

    private void addRootNode(ZooKeeper zk) {
        try {
            String rootPath = zooKeeperProperties.getRootPath();
            Stat exists = zk.exists(rootPath, false);
            if (exists == null) {
                zk.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("create zookeeper node error,cause:", e);
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            String dataPath = zooKeeperProperties.getRootPath() + zooKeeperProperties.getDataPath();
            String path = zk.create(dataPath, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException | InterruptedException e) {
            log.error("create zookeeper node error,cause:", e);
        }
    }

}
