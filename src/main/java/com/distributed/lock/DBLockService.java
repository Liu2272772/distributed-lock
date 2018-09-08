package com.distributed.lock;


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
import com.distributed.lock.bean.LockBean;
import com.distributed.lock.dao.LockDao;

@Service("DBLockService")
public class DBLockService implements LockService{
	
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private LockBean lockBean=new LockBean(1);
	
	private ReentrantLock lock=new ReentrantLock();
		
	@Autowired
    private DataSourceTransactionManager txManager;
	
	@Autowired
	private LockDao lockDao;

	public  Boolean tryLock(LockBean lockBean) {
		lockDao.tryLock(lockBean);
		return true;
	}

	public  Boolean unLockCommit(TransactionStatus status) {
		txManager.commit(status);
		return true;
	}

	public Boolean unLockRollBack(TransactionStatus status) {
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
	public Object doService(ProceedingJoinPoint pjp) throws Throwable {
		Object obj=null;
		//设置事务隔离级别相关属性
		DefaultTransactionDefinition def=new DefaultTransactionDefinition();
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	    TransactionStatus status=txManager.getTransaction(def);
		try{
			//保证线程同步
			lock.lock();
			
		    //获取锁属性,如果业务方法参数没有LockBean对象，那么使用默认当前lockBean
		    LockBean paramLockBean=deduceLockBean(pjp);
	    	//数据库锁实现方式采用行锁。所以必须保证数据库中有这条数据来锁定.
			ensureDataExits(paramLockBean);
			
			//试图获取锁，该方法会阻塞。直到获取到数据库锁
	    	tryLock(paramLockBean);
	    	//获取到锁后执行业务方法
		    obj=pjp.proceed(pjp.getArgs());
		    //业务方法执行完成提交事务。防止阻塞其他需要获取锁的线程或者服务实例。
		    unLockCommit(status);
	    }catch(Exception e){
	    	//e.printStackTrace();
	    	//出现异常回滚事务。防止阻塞其他需要获取锁的线程或者服务实例。
	    	unLockRollBack(status);
	    }finally{
	    	//线程锁释放
	    	lock.unlock();
	    	
	    }
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
