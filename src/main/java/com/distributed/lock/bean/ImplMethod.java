package com.distributed.lock.bean;

/**
 * 锁实现方式选择
 * RDB 选择关系型数据库 可以时MySQL、Oracle
 * ZK 选择Zookeeper
 * REDIS 选择Redis
 * @author zouhuixing
 *
 */
public enum ImplMethod {
	RDB,ZK,REDIS
}
