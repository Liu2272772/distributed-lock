package com.distributed.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

import com.distributed.lock.annotation.DistributedLock;

@Service("ZKLockService")
public class ZKLockService implements LockService{

	@Override
	public Object doService(ProceedingJoinPoint pjp,DistributedLock lockAnnotation) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
