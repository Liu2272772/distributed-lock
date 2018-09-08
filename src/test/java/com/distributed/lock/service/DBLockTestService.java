package com.distributed.lock.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.distributed.lock.annotation.DistributedLock;
import com.distributed.lock.bean.ImplMethod;

@Service
public class DBLockTestService {
	@DistributedLock(type=ImplMethod.RDB)
	public String sayHello(String content){
		Long id=Thread.currentThread().getId();
		System.err.println("Thread ID:"+id);
		try {
			
			Thread.sleep(1000);
			System.out.println("Thread ID:"+id);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Random ra =new Random();
		int random=ra.nextInt(10);
		if(random>8){
		    throw new NullPointerException();
		}
		System.err.println("Thread ID:"+id);
		return "Hello,"+content;
		
	}

}
