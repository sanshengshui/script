package com.sanshengshui.registry;

/**
 * @InterfaceName serviceregistry
 * @author 穆书伟
 * @description 服务注册接口
 * @date 2017年7月27号 下午17:05:43
 */
public interface ServiceRegistry {
    /**
     * 注册服务名称与服务地址
     * @param serviceName  服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName,String serviceAddress);
}
