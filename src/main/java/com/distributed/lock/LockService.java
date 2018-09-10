package com.distributed.lock;

import org.aspectj.lang.ProceedingJoinPoint;

import com.distributed.lock.annotation.DistributedLock;

public interface LockService {
	public Object doService(ProceedingJoinPoint pjp,DistributedLock lockAnnotation) throws Throwable;

}
