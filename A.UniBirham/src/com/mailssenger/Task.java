package com.mailssenger;

import java.lang.ref.WeakReference;
import android.content.Context;

/**
 * TASK Logic Model, the main service will invoke this 
 * @author 
 * @param 
 * @return
 */
public class Task {
	
	private int priority=0;    				//Task Proority,default 0
	private Class runClass;					//Class Object that run							
	private String runMethod;				//function that run								
	private WeakReference<Context> mContext;//Actvity context that call Task										
	private Object[] params;				//parameters																	
	
	/*
	 * Constructor without any parameters 
	 */
	public Task() {
		super();
	}
	
	/*
	 * Constructor with the Context of activiy
	 */
	public Task(Context mContext) {
		super();
		//using weakReference can point to context for the activity even 
		//when the content of reference has change!
		WeakReference<Context> ContextReference = new WeakReference<Context>(
				mContext);
		this.mContext=ContextReference;
	}
	
	/*
	 * Constructor with the Context of activiy and Priority
	 */
	public Task(Context mContext,int priority) {
		super();  
		//using weakReference can point to context for the activity even 
		//when the content of reference has change!
		WeakReference<Context> ContextReference = new WeakReference<Context>(
				mContext);
		this.mContext=ContextReference;
		this.priority=priority;
	}
	
	/**
	 * Set the class name, function name and parameters that you want to run
	 */
	public void setMethod(Class runClass,String runMethod,Object... params) {
		this.runClass = runClass;
		this.runMethod=runMethod;
		this.params=params;
	}
	
	/**
	 * set task context for callback
	 */
	public void setContext(Context mContext) {
		WeakReference<Context> ContextReference = new WeakReference<Context>(
				mContext);
		this.mContext = ContextReference;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public Class getRunClass() {
		return runClass;
	}

	public String getRunMethod() {
		return runMethod;
	}
	
	/*
	 * get all the parameters
	 */
	public Object[] getParams() {
		return params;
	}
	
	public Context getContext() {
		return mContext.get();
	}

}
