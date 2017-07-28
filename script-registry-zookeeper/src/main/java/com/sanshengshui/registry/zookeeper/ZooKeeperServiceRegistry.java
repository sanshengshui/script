package com.sanshengshui.registry.zookeeper;

import com.sanshengshui.registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName ZooKeeperServiceRegistry
 * @Description 基于Zookeeper的服务发现接口实现
 * @author 穆书伟
 * @Date 2017年7月27号 下午18:03:47
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry{

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private final ZkClient zkClient;

    public ZooKeeperServiceRegistry(String zkAddress){
        //创建ZooKeeper客户端
        zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
    }
    @Override
    public void register(String serviceName, String serviceAddress) {
        //创建registry节点(持久)
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if(!zkClient.exists(registryPath)){
            zkClient.createPersistent(registryPath);
            LOGGER.debug("create registry node: {}",registryPath);
        }
        //创建service节点(持久)
        String servicePath = registryPath + "/" +serviceName;
        if(!zkClient.exists(servicePath)){
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node:{}",servicePath);
        }
        //创建address节点(临时)
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath,serviceAddress);
        LOGGER.debug("create address node:{}",addressNode);
    }
}
