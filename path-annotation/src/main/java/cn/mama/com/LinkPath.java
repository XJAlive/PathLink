package cn.mama.com;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对外提供服务方法注解 Created by xiej on 2020/11/18
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface LinkPath {

    /**
     * 设置别名
     */
    String alias();
}
