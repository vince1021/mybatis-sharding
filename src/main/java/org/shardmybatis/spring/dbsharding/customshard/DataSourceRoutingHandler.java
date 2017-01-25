package org.shardmybatis.spring.dbsharding.customshard;

public interface DataSourceRoutingHandler {
	
	//获取数据源分库
	public String getDataSource(Object param);
	
	//获取分表表名
	public String getTableName(Object param);

}
