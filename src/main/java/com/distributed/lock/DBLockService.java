package com.distributed.lock;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.distributed.lock.annotation.DistributedLock;
import com.distributed.lock.bean.LockBean;
import com.distributed.lock.dao.LockDao;
import com.distributed.lock.exception.TimeOutException;

@Service("DBLockService")
public class DBLockService implements LockService{
	
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private LockBean lockBean=new LockBean(1);
	
	private ThreadLock locks=new ThreadLock();
		
	@Autowired
    private DataSourceTransactionManager txManager;
	
	@Autowired
	private LockDao lockDao;

	public  Boolean tryDistributedLock(LockBean lockBean) {
		lockDao.tryLock(lockBean);
		return true;
	}

	public  Boolean unDistributedLockCommit(TransactionStatus status) {
		txManager.commit(status);
		return true;
	}

	public Boolean unDistributedLockRollBack(TransactionStatus status) {
		txManager.rollback(status);
		return true;
	}
	
	private LockBean ensureDataExits(LockBean lockBean){
		LockBean qryBean=new LockBean();
		qryBean=lockDao.queryLock(lockBean);
		if(null==qryBean){
			lockDao.addLock(lockBean);
		}
		return qryBean;
	}

	@Override
	public Object doService(ProceedingJoinPoint pjp,DistributedLock lockAnnotation) throws Throwable {
		Object obj=null;
		//设置事务隔离级别相关属性
		DefaultTransactionDefinition def=new DefaultTransactionDefinition();
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	    TransactionStatus status=null;
	    
	    //获取锁属性,如果业务方法参数没有LockBean对象，那么使用默认当前lockBean
	    LockBean paramLockBean=deduceLockBean(pjp);
	    ReentrantLock lock=locks.getLock(paramLockBean);
	    //保证线程同步
	    Long startTime=System.currentTimeMillis();
		boolean isGetLock=lock.tryLock(lockAnnotation.timeOut(),TimeUnit.MILLISECONDS);
		Long spendTime=System.currentTimeMillis()-startTime;
		boolean isTimeOut=false;
		try{
			//获取到锁肯定没有超时
			if(isGetLock){
				def.setTimeout(Integer.valueOf(String.valueOf(lockAnnotation.timeOut()-spendTime)));
				status=txManager.getTransaction(def);
				return doServiceLogic(pjp,paramLockBean,obj,status);	
			//否则超时
			}else{
				isTimeOut=true;
				throw new TimeOutException("获取锁超时异常");
			}
	    }catch(Exception e){
	    	
	    	if(e instanceof TimeOutException){
	    		System.err.println("获取锁超时异常");
	    	}else{
	    		e.printStackTrace();
	    	}
	    	//出现异常回滚事务。防止阻塞其他需要获取锁的线程或者服务实例。
	    	if(!isTimeOut){
	    		unDistributedLockRollBack(status);
	    	}
	    }finally{
	    	//线程锁释放
	    	if(isGetLock){
	    		lock.unlock();
	    	}
	    	
	    	
	    }
		return obj;
	}
	private Object doServiceLogic(ProceedingJoinPoint pjp,LockBean lockBean,Object obj,TransactionStatus status) throws Throwable{
		//数据库锁实现方式采用行锁。所以必须保证数据库中有这条数据来锁定.
		ensureDataExits(lockBean);
		
		//试图获取锁，该方法会阻塞。直到获取到数据库锁
    	tryDistributedLock(lockBean);
    	//获取到锁后执行业务方法
	    obj=pjp.proceed(pjp.getArgs());
	    //业务方法执行完成提交事务。防止阻塞其他需要获取锁的线程或者服务实例。
	    unDistributedLockCommit(status);
	    return obj;
	}
	private LockBean deduceLockBean(ProceedingJoinPoint pjp){
		LockBean paramLockBean=null;
    	Object[] args=pjp.getArgs();
    	for(Object pram:args){
			if(pram instanceof LockBean){
				paramLockBean=(LockBean)pram;
				break;
			}
		}
		if(null==paramLockBean){
			paramLockBean=lockBean;
		}
		return paramLockBean;
	}

	
}
