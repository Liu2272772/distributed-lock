package com.distributed.lock;

import org.aspectj.lang.ProceedingJoinPoint;

public interface LockService {
	public Object doService(ProceedingJoinPoint pjp) throws Throwable;

}
