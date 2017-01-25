# mybatis的分库分表方案

1、只分表不分库
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource" />
        <property name="configLocation" value="classpath:/config/mybatis-config.xml"></property>
		<property name="typeAliasesPackage" value="com.test.testpro.persistence.po"></property>
		<property name="mapperLocations" value="classpath*:/sqlmapper/*.xml"></property>
		<property name="plugins">
			<array>
				<bean class="org.shardmybatis.spring.dbsharding.interceptor.StatementHandlerInterceptor" />
			</array>
		</property> 
    </bean>
    <bean name="mapperScannerConfigurer" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<property name="basePackage" value="com.test.testpro.persistence.dao"></property>
		<property name="sqlSessionFactory" ref="sqlSessionFactory" />
	</bean>

2、分库分表
    <bean id="shardDataSource" class="org.shardmybatis.spring.dbsharding.ShardDataSource">
		<property name="targetDataSources">
			<map key-type="java.lang.String">
				<entry key="dataSource0" value-ref="trade0000"></entry>
				<entry key="dataSource1" value-ref="trade0001"></entry>
				<entry key="dataSource2" value-ref="trade0002"></entry>
				<entry key="dataSource3" value-ref="trade0003"></entry>
			</map>
		</property>
	</bean>
	<bean id="sqlSessionFactoryShard1" class="org.shardmybatis.spring.dbsharding.SqlSessionFactoryShardBean">
		<property name="dataSource" ref="shardDataSource" />
		<property name="configLocation" value="classpath:/config/mybatis-config.xml"></property>
		<property name="typeAliasesPackage" value="com.test.testpro.persistence.po"></property>
		<property name="mapperLocations" value="classpath*:/sdsqlmapper/*.xml"></property>
		<property name="plugins">
			<array>
				<bean class="org.shardmybatis.spring.dbsharding.interceptor.StatementHandlerInterceptor" />
			</array>
		</property>   
	</bean>
	<bean name="mapperShardScannerConfigurer" class="org.shardmybatis.spring.dbsharding.MapperScannerShardConfigurer">
		<property name="basePackage" value="com.test.testpro.persistence.dao"></property>
		<property name="sqlSessionFactory" ref="sqlSessionFactoryShard1" />
	</bean>

