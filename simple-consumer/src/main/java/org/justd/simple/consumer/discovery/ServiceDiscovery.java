package org.justd.simple.consumer.discovery;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.justd.simple.common.property.ZooKeeperProperties;
import org.justd.simple.common.util.ZookeeperUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zhangjd
 * @title: ServiceDiscovery
 * @description:
 * @date 2020/1/415:24
 */
@Slf4j
public class ServiceDiscovery {

    private volatile List<String> nodes = new ArrayList<>();

    private ZooKeeperProperties zooKeeperProperties;

    public ServiceDiscovery(ZooKeeperProperties zooKeeperProperties) {
        this.zooKeeperProperties = zooKeeperProperties;
        String address = zooKeeperProperties.getAddress();
        int timeout = zooKeeperProperties.getTimeout();
        ZooKeeper zooKeeper = ZookeeperUtils.connectServer(address, timeout);
        if (zooKeeper != null){
            watchNode(zooKeeper);
        }
    }

    public String discover(){
        String data = null;
        int size = nodes.size();
        if (size >0 ){
            if (size == 1){
                data = nodes.get(0);
            }else {
                data = nodes.get(ThreadLocalRandom.current().nextInt(size));
            }
        }
        return data;
    }

    private void watchNode(final ZooKeeper zk){
        try {
            Watcher childrenNodeChangeWatch = event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged){
                    watchNode(zk);
                }
            };
            String rootPath = zooKeeperProperties.getRootPath();
            List<String> nodeList = zk.getChildren(rootPath, childrenNodeChangeWatch);
            List<String> nodes = new ArrayList<>();
            for (String node : nodeList){
                byte[] bytes = zk.getData(rootPath + "/" + node, false, null);
                nodes.add(new String(bytes, StandardCharsets.UTF_8));
            }
            log.info("node data:{}",nodes);
            this.nodes = nodes;
        } catch (KeeperException | InterruptedException e) {
            log.error("watch node error, curse:",e);
        }
    }
}
