package com.distributed.lock.exception;

public class TimeOutException extends Exception{

	private static final long serialVersionUID = -2802676051478872671L;
	
	public TimeOutException(String msg){
		super(msg);
	}

}
