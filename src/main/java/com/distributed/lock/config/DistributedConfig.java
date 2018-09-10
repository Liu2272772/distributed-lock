package com.distributed.lock.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"com.distributed.lock"})
@MapperScan(basePackages = {"com.distributed.lock"})
public class DistributedConfig {
	
}
