package com.sanshengshui.server;

import com.sanshengshui.bean.RpcRequest;
import com.sanshengshui.bean.RpcResponse;
import com.sanshengshui.util.base.StringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @ClassName RpcServerHandler
 * @author 穆书伟
 * @Description RPC服务端处理器(用于处理RPC请求)
 * @Date 2017年7月28号 下午15:50:56
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private final Map<String,Object> handlerMap;

    public RpcServerHandler(Map<String,Object> handlerMap){
        this.handlerMap = handlerMap;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("server caught exception",cause);


        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        //创建并初始化RPC响应对象
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        }catch (Exception e){
            LOGGER.error("handle result failure",e);
            response.setException(e);
        }
        //写入RPC响应对象并自动关闭连接
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Exception{
        //获取服务对象
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getServiceVersion();
        if (StringUtil.isNotEmpty(serviceVersion)){
            serviceName += "-" + serviceVersion;
        }
        Object serviceBean = handlerMap.get(serviceName);
        if(serviceBean == null){
            throw  new RuntimeException(String.format("can not find service bean by key: %s",serviceName));
        }
        //获取反射调用所需的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName,parameterTypes);
        return serviceFastMethod.invoke(serviceBean,parameters);
    }
}
