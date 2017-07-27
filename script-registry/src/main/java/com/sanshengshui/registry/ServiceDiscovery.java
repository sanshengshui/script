package com.sanshengshui.registry;

/**
 * @InterfaceName ServiceDiscovery
 * @description 服务发现接口
 * @author 穆书伟
 * @date 2017年7月27号 下午16:59:37
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     *@return 服务地址
     */
    String discover(String serviceName);
}
