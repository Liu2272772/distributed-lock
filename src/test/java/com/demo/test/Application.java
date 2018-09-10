package com.demo.test;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.distributed.lock.DBLockService;
import com.distributed.lock.annotation.DistributedLockSupport;
import com.distributed.lock.bean.LockBean;


@DistributedLockSupport
@SpringBootApplication
public class Application {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context=SpringApplication.run(Application.class,args);
		DBLockService service=context.getBean(DBLockService.class);
		System.out.println(service.tryDistributedLock(new LockBean(1)));
	}

}
