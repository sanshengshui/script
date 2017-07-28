package com.sanshengshui.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName RpcService
 * @author 穆书伟
 * @Description RPC服务注册(标注在服务实现类上)
 * @Date 2017年7月28日 下午16:41:53
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 服务接口类
     */
    Class<?> value();
    /**
     *服务版本号
     */
    String version() default "";
}
