package com.distributed.lock.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.distributed.lock.bean.LockBean;


@Mapper
public interface LockDao {
	@Insert("INSERT INTO locktable(lockNum) VALUES (#{lockNum})")
	public Integer addLock(LockBean lockBean);
	
	@Select("SELECT * FROM locktable WHERE lockNum=#{lockNum} for update")
	public LockBean tryLock(LockBean lockBean);
	
	@Select("SELECT * FROM locktable WHERE lockNum=#{lockNum}")
	public LockBean queryLock(LockBean lockBean);

}
