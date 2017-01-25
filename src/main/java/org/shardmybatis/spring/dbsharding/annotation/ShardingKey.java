package org.shardmybatis.spring.dbsharding.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardingKey {
	//定义需要替换的字符串
}
