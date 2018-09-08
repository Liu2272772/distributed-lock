package com.distributed.lock.bean;

public class LockBean {
    private Integer lockNum;
	
	private Long lockTime;

	public LockBean(){}
	public LockBean(Integer lockNum){
		this.lockNum=lockNum;
	}
	public Integer getLockNum() {
		return lockNum;
	}

	public void setLockNum(Integer lockNum) {
		this.lockNum = lockNum;
	}

	public Long getLockTime() {
		return lockTime;
	}

	public void setLockTime(Long lockTime) {
		this.lockTime = lockTime;
	}

}
