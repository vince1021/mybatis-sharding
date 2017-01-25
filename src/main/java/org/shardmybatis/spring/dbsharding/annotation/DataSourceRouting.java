package org.shardmybatis.spring.dbsharding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.shardmybatis.spring.dbsharding.customshard.DataSourceRoutingHandler;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceRouting {
    /**
     * 静态绑定该Mapper对应的数据源
     *
     * @return 绑定的数据源的名称
     */
    String value() default "";

    /**
     * 指定运行时路由处理的Handler，动态指定对应的数据源。<b>注意:此方式不能支持事务！</b>
     *
     * @return
     */
    Class<? extends DataSourceRoutingHandler> handler() ;

}
