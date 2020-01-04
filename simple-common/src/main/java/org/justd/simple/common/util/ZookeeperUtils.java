package org.justd.simple.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Zhangjd
 * @title: ZookeeperUtils
 * @description:
 * @date 2020/1/321:30
 */
@Slf4j
public class ZookeeperUtils {

    public static ZooKeeper connectServer(String registryAddress, int timeout) {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = null;
        try {
            Watcher watcher = watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            };
            zk = new ZooKeeper(registryAddress, timeout, watcher);
            //若计数器不为0，则等待
        } catch (IOException e) {
            log.error("get zookeeper connect error, cause:", e);
        }

        return zk;
    }
}
