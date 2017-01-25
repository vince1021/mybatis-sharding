package org.shardmybatis.spring.dbsharding.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.shardmybatis.spring.dbsharding.ReflectUtil;
import org.shardmybatis.spring.dbsharding.annotation.DataSourceRouting;
import org.shardmybatis.spring.dbsharding.annotation.ShardingKey;
import org.shardmybatis.spring.dbsharding.customshard.DataSourceRoutingHandler;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class }) })  
public class StatementHandlerInterceptor implements Interceptor {  
  
	/**
	 * //对于StatementHandler其实只有两个实现类，一个是RoutingStatementHandler，另一个是抽象类BaseStatementHandler，    
        //BaseStatementHandler有三个子类，分别是SimpleStatementHandler，PreparedStatementHandler和CallableStatementHandler，    
        //SimpleStatementHandler是用于处理Statement的，PreparedStatementHandler是处理PreparedStatement的，而CallableStatementHandler是    
        //处理CallableStatement的。Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个    
        //StatementHandler类型的delegate属性，RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、    
        //PreparedStatementHandler或CallableStatementHandler，在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。    
        //我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，又因为Mybatis只有在建立RoutingStatementHandler的时候    
        //是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象。
	 */
    @Override  
    public Object intercept(Invocation invocation) throws Throwable {  

		if (invocation.getTarget() instanceof RoutingStatementHandler) {

			StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectUtil.getFieldValue(statementHandler,"delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
			//System.out.println("mappedStatement:" + mappedStatement.getId());

			BoundSql boundSql = statementHandler.getBoundSql();
			String sql = boundSql.getSql();
			Object paramobj = boundSql.getParameterObject();
			//System.out.println("papram:" + JSONUtils.toJSONString(paramobj));

			String dbNameStr = "";
			String cm = mappedStatement.getId();
			int index = cm.lastIndexOf(".");
			if (index != -1) {
				Class<?> mapperClass = Class.forName(cm.substring(0, index));// 静态加载类
				
				final DataSourceRouting dataSourceRouting = mapperClass.getAnnotation(DataSourceRouting.class);
				if(dataSourceRouting != null){
					Method mapperMethod = mapperClass.getMethod(cm.substring(index + 1), paramobj.getClass());
					// 获取参数上的注解
					Object param = null;
					Annotation[][] an = mapperMethod.getParameterAnnotations();
					for (int i = 0; i < an.length; i++) {
						Annotation[] arr2 = an[i];
						for (int c = 0; c < arr2.length; c++) {
							if (arr2[c] instanceof ShardingKey) {
								param = paramobj;
							}
						}
					}
					if (param == null) {
						Field[] fields = paramobj.getClass().getDeclaredFields();
						for (Field f : fields) {
							String filedName = f.getName();
							// 获取属性上的指定类型的注释
							Annotation annotation = f.getAnnotation(ShardingKey.class);
							if (annotation != null) {
								param = ReflectUtil.getFieldValue(paramobj, filedName);
							}
						}
					}

					
					if (param != null) {
						DataSourceRoutingHandler dataSourceRoutingHandler = dataSourceRouting.handler().newInstance();
						dbNameStr = dataSourceRoutingHandler.getTableName(param);
						if(dbNameStr != null){
							ReflectUtil.setFieldValue(boundSql, "sql", sql.replaceAll("\\$SHARD\\$", dbNameStr));
						}
					}
				}
				
			}
			
		}

		return invocation.proceed();
    }  
  
   
  
    @Override  
    public Object plugin(Object target) {  
        return Plugin.wrap(target, this);  
    }  
    @Override  
    public void setProperties(Properties properties) {  
  
    }  
} 
