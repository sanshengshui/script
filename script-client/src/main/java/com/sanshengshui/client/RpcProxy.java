package com.sanshengshui.client;

import com.sanshengshui.bean.RpcRequest;
import com.sanshengshui.bean.RpcResponse;
import com.sanshengshui.registry.ServiceDiscovery;
import com.sanshengshui.util.base.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
/**
 * @ClassName RpcProxy
 * @Description RPC代理(用于创建RPC服务代理)
 * @author 穆书伟
 * @Date 2017年7月28日 下午12:41:55
 */
public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serviceAddress){
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }



    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass,final String serviceVersion){
        //创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),

                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //创建RPC请求对象并设置请求属性
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setServiceVersion(serviceVersion);
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        if (serviceDiscovery !=null){
                            String serviceName = interfaceClass.getName();
                            if(StringUtil.isNotEmpty(serviceVersion)){
                                serviceName += "-"+ serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);
                            LOGGER.debug("discover service: {}=》{}",serviceName,serviceAddress);
                        }
                        if(StringUtil.isNotEmpty(serviceAddress)){
                            throw new RuntimeException("server address is empty");
                        }
                        //从RPC服务地址中解析主机名与端口号
                        String[] array = StringUtil.split(serviceAddress,":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        //创建RPC客户端对象并发送RPC请求
                        RpcClient client = new RpcClient(host,port);
                        long time = System.currentTimeMillis();
                        RpcResponse response = client.send(request);
                        LOGGER.debug("time:{}ms",System.currentTimeMillis() - time);
                        if(response == null){
                            throw new RuntimeException("response is null");
                        }
                        //返回RPC响应结果
                        if(response.hasException()){
                            throw response.getException();
                        }else {
                            return response.getResult();
                        }

                    }
                }
        );

    }

}




