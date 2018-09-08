package com.distributed.lock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.distributed.lock.service.DBLockTestService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class DBLockTest {
	@Autowired
	private DBLockTestService dbLockTestService;
	
	@Test
	public void testOne(){
		for(int i=0;i<100;i++){
			new Thread(new Runnable(){
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					dbLockTestService.sayHello("Bob");
				}
				
			}).start();
			
			new Thread(new Runnable(){
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					dbLockTestService.sayHello("POB");
				}
				
			}).start();
		}
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
