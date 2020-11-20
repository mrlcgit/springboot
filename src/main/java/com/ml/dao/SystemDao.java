package com.ml.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SystemDao {
	
	@PersistenceContext
    private EntityManager entityManger;
	
	public List<Object[]> getBySql(String sql) {
		List<Object[]> list = entityManger.createNativeQuery(sql).getResultList();
		return list;
	}
	
	public Object getSingleResultBySql(String sql) {
		Object obj = entityManger.createNativeQuery(sql).getSingleResult();
		return obj;
	}
	
}
