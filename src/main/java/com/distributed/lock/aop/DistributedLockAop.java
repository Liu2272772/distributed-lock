package com.distributed.lock.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.distributed.lock.DBLockService;
import com.distributed.lock.LockService;
import com.distributed.lock.annotation.DistributedLock;
import com.distributed.lock.bean.ImplMethod;


@Aspect
@Component
public class DistributedLockAop implements ApplicationContextAware{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ApplicationContext applicationContext;
	
	@Pointcut("@annotation(com.distributed.lock.annotation.DistributedLock)")
    private void aspectJMethod(){};
    
	/**
	 * 开始执行分布式锁逻辑。
	 * @param pjp
	 * @return
	 */
	@Around(value = "aspectJMethod()")
    public Object doConcurrentOperation(ProceedingJoinPoint pjp) { 
		//首先获取同步方法的注解。
		DistributedLock lockAnnotation=getDistributedLockAnnotation(getMethod(pjp));
		//通过注解获取实现的服务类型
		LockService lockService=selectLogicLockService(lockAnnotation);
	    try {
	    	//调用服务
			return lockService.doService(pjp);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
    }
	
	
	private LockService selectLogicLockService(DistributedLock lockAnnotation){
		if(ImplMethod.RDB == lockAnnotation.type()){
	    	return (DBLockService)applicationContext.getBean("DBLockService");
	    }else{
	    	logger.error("分布式锁实现方式选取失败,请检查注解是否正确。。。");
	    	return null;
	    }
	}
	/**
	 * 通过ProceedingJoinPoint对应的实例获取到当前执行方法
	 * @param pjp
	 * @return
	 */
	private Method getMethod(ProceedingJoinPoint pjp){
		Signature signature = pjp.getSignature();    
	    MethodSignature methodSignature = (MethodSignature)signature;    
	    return methodSignature.getMethod();  
	}
	/**
	 * 获取到方法上对应的DistributedLock注解
	 * @param method
	 * @return
	 */
	private DistributedLock getDistributedLockAnnotation(Method method){
		Annotation[] annotations = method.getDeclaredAnnotations();
	    DistributedLock  methodAnnotation=null;
	    for(Annotation annotation: annotations){
	    	
	    	if(annotation instanceof DistributedLock){
	    		methodAnnotation=(DistributedLock)annotation;
	    		break;
	    	}
	    	
	    }
	    return methodAnnotation;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
		
	}

}
