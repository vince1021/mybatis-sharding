package org.shardmybatis.spring.dbsharding.interceptor;

import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.shardmybatis.spring.dbsharding.ReflectUtil;

/**
 * 拦截器Executor 废弃
 *
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class,Object.class }) })  
public class ExecutorInterceptor implements Interceptor {  
  
    @Override  
    public Object intercept(Invocation invocation) throws Throwable {  
//    	if(invocation.getTarget() instanceof BaseExecutor){ 
//    		System.out.println("BaseExecutor Interceptor---------------");
//    	}
    	if(invocation.getTarget() instanceof CachingExecutor){ 
    		System.out.println("CachingExecutor Interceptor---------------");
    		CachingExecutor cachingExecutor = (CachingExecutor)invocation.getTarget();    
    		
    		//ReflectUtil.getFieldValue(cachingExecutor, fieldName)
    		
    		
    	}
    	
    	
    
    	
    	
    	System.out.println("BaseExecutor Interceptor---------------"+invocation.getTarget().getClass().getName());
    	
		Object[] args = invocation.getArgs();
		System.out.println("-----------参数拦截---------------------------------------------------");
		System.out.println("02 当前线程ID:" + Thread.currentThread().getId());
		// 遍历处理所有参数，update方法有两个参数，参见Executor类中的update()方法。
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			String className = arg.getClass().getName();
			System.out.println(i + " 参数类型：" + className);

			// 第一个参数处理。根据它判断是否给“操作属性”赋值。
			if (arg instanceof MappedStatement) {// 如果是第一个参数 MappedStatement
				MappedStatement ms = (MappedStatement) arg;
				SqlCommandType sqlCommandType = ms.getSqlCommandType();
				System.out.println("操作类型：" + sqlCommandType);
				
				System.out.println("DatabaseId：" +ms.getDatabaseId());
				
				
				
//				if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {// 如果是“增加”或“更新”操作，则继续进行默认操作信息赋值。否则，则退出
//					continue;
//				} else {
//					break;
//				}
			}

			// 第二个参数处理。（只有第二个程序才能跑到这）
			if (arg instanceof Map) {// 如果是map，有两种情况：（1）使用@Param多参数传入，由Mybatis包装成map。（2）原始传入Map
				System.out.println("这是一个包装过的类型!");
//				Map map = (Map) arg;
//				for (Object obj : map.values()) {
//					setProperty(obj);
//				}
			} else {// 原始参数传入
//				setProperty(arg);
			}

		}
    	 
    	
    	System.out.println("ExecutorInterceptor---------------");
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
