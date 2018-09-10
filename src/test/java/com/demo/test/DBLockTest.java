package com.demo.test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.test.service.DBLockTestService;
import com.distributed.lock.bean.LockBean;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class DBLockTest {
	@Autowired
	private DBLockTestService dbLockTestService;
	
	ExecutorService pool = Executors.newFixedThreadPool(10);
	
	@Test
	public void testNOLockBeanParam(){
		
		for(int i=0;i<100;i++){
			pool.execute(new Runnable(){

				@Override
				public void run() {
					dbLockTestService.sayHello("Bob");
				}
			});
		}
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testHaveLockBean(){
		for(int i=0;i<1000;i++){
			pool.execute(new Runnable(){

				@Override
				public void run() {
					Random ra =new Random();
					int random=ra.nextInt(100);
					// TODO Auto-generated method stub
					dbLockTestService.sayHello("Bob",new LockBean(random));
				}
				
			});
		}
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
