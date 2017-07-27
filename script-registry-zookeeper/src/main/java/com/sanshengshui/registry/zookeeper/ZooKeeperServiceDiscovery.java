package com.sanshengshui.registry.zookeeper;

import com.sanshengshui.registry.ServiceDiscovery;
import com.sanshengshui.util.base.CollectionUtil;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName ZooKeeperServiceDiscovery
 * @Description 基于Zookeeper的服务发现接口实现
 * @author 穆书伟
 * @Date 2017年7月27号 下午17:22
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private String zkAddress;

    public ZooKeeperServiceDiscovery(String zkAddress){
        this.zkAddress = zkAddress;
    }
    @Override
    public String discover(String serviceName) {
       //创建ZooKeeper客户端
        ZkClient zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        try{
            //获取service节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)){
                throw new RuntimeException(String.format("can not find any service node on path: %s",servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if(CollectionUtil.isEmpty(addressList)){
                throw new RuntimeException(String.format("can not find any address node on path: %s",servicePath));
            }
            //获取address节点
            String address;
            int size = addressList.size();
            if (size == 1){
                //若只有一个地址，则获取该地址
                address = addressList.get(0);
                LOGGER.debug("get only address node:{}",address);
            }else {
                //若存在多个地址，则随机获取一个地址
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("get random address node: {}",address);
            }
            //获取address节点的值
            String addressPath = servicePath + "/" +address;
            return zkClient.readData(addressPath);
        }finally {
            zkClient.close();
        }
    }
}
