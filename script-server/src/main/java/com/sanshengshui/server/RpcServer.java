package com.sanshengshui.server;

import com.sanshengshui.bean.RpcRequest;
import com.sanshengshui.bean.RpcResponse;
import com.sanshengshui.codec.RpcDecoder;
import com.sanshengshui.codec.RpcEncoder;
import com.sanshengshui.registry.ServiceRegistry;
import com.sanshengshui.util.base.StringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Rpc服务器
 * @author 穆书伟
 * @description RPC服务器(用于发布RPC服务)
 * @Date 2017年7月28日 16:48:18
 */
public class RpcServer implements ApplicationContextAware,InitializingBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    /**
     * 存放服务名与服务对象之间的映射关系
     */
    private Map<String,Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress){
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress,ServiceRegistry serviceRegistry){
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建并初始化Netty服务端Bootstrap对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class));//解码RPC请求
                    pipeline.addLast(new RpcEncoder(RpcResponse.class));//解码RPC响应
                    pipeline.addLast(new RpcServerHandler(handlerMap));//处理RPC请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG,1024);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            //获取RPC服务器的IP地址与端口号
            String[] addressArray = StringUtil.split(serviceAddress,":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            //启动RPC服务器
            ChannelFuture future = bootstrap.bind(ip,port).sync();
            //注册RPC服务地址
            if(serviceRegistry != null){
                for (String interfaceName : handlerMap.keySet()){
                    serviceRegistry.register(interfaceName,serviceAddress);
                    LOGGER.debug("register service: {} => {}",interfaceName,serviceAddress);
                }
            }
            LOGGER.debug("server started on port {}",port);
            //关闭RPC服务器
            future.channel().closeFuture().sync();

        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
