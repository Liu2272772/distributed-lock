package com.distributed.lock;

import java.util.concurrent.locks.ReentrantLock;

import com.distributed.lock.bean.LockBean;

public class ThreadLock {
	private ReentrantLock[] locks=new ReentrantLock[]{	new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock()
	};
	
	public ReentrantLock getLock(LockBean lockBean){
		int lockNum=lockBean.getLockNum();
		return locks[lockNum%10];
	}
}
