package com.mailssenger.db;

import java.util.List;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.mailssenger.CommonApplication;
import com.mailssenger.model.MsgModel;

public class BaseDB {

	protected DbUtils db;
	
	protected <T> T findById(Class<T> entityType, Object idValue){
		T entity = null;
		try {
			entity = db.findById(entityType, idValue);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return entity;
	}
	
	protected <T> List<T> findAll(Class<T> entityType){
		List<T> entities = null;
		try {
			entities = db.findAll(entityType);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return entities;	       
	}

	//construct;
	public BaseDB(Context context) {
		super();
		
		this.db = DbUtils.create(context, CommonApplication.DB_NAME);
		db.configAllowTransaction(true);
		db.configDebug(true);		
	}
	
	/*
	 * save one conversation item
	 */
	public void save(Object entity){
		try {
//			db.save(entity);
			db.saveOrUpdate(entity);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void delete(Object entity){
		try {
//			db.save(entity);
			db.delete(entity);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void update(Object entity){
		
		try {
			db.update(entity);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void saveOrUpdate(Object entity){
		
		try {
			db.saveOrUpdate(entity);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateAll(List<?> entities){
		
		try {
			db.updateAll(entities);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
