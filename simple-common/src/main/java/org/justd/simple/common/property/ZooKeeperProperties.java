package org.justd.simple.common.property;

/**
 * @author Zhangjd
 * @title: ZooKeeperProperties
 * @description:
 * @date 2020/1/321:42
 */
public class ZooKeeperProperties {
    private String address;

    private String rootPath;

    private int timeout;

    private String dataPath;

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
