package com.sanshengshui.client;

import com.sanshengshui.bean.RpcRequest;
import com.sanshengshui.bean.RpcResponse;
import com.sanshengshui.codec.RpcDecoder;
import com.sanshengshui.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName RpcClient
 * @Description Rpc客户端(用于发送RPC请求)
 * @author 穆书伟
 * @Date 2017年7月28号 下午13:50:22
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private final String host;

    private final int port;

    private RpcResponse rpcResponse;

    public RpcResponse send(RpcRequest request) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            //创建并初始化Netty客户端Bootstrap对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class));//编码RPC请求
                    pipeline.addLast(new RpcDecoder(RpcResponse.class));//解码RPC响应
                    pipeline.addLast(RpcClient.this);//处理RPC相应
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            //连接RPC服务器
            ChannelFuture future = bootstrap.connect(host,port).sync();
            //写入RPC请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            //返回RPC响应对象
            return  rpcResponse;
        }finally {
            group.shutdownGracefully();
        }
    }

    public RpcClient(String host,int port){
        this.host = host;
        this.port = port;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception",cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.rpcResponse = rpcResponse;
    }
}
