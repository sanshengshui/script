package com.sanshengshui.server;

import com.sanshengshui.util.base.ConcurrentLRUHashMap;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

/**
 * @ClassName NettyChannelLRUMap
 * @author 穆书伟
 * @Description netty链路通道过期策略
 * @Date 2017年8月10号 下午20:20:12
 */
public class NettyChannelLRUMap {

    private static ConcurrentLRUHashMap<String,SocketChannel> map=new ConcurrentLRUHashMap<String, SocketChannel>(1024);
    public static void add(String clientId,SocketChannel socketChannel){
        map.put(clientId,socketChannel);
    }
    public static Channel get(String clientId){
        return map.get(clientId);
    }
    public static void remove(SocketChannel socketChannel){
        for (Map.Entry entry:map.entrySet()){
            if (entry.getValue()==socketChannel){
                map.remove(entry.getKey());
            }
        }
    }

}
