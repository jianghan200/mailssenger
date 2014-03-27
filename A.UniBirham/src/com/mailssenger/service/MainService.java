package com.mailssenger.service;
/*
 * taskqueue backend thread logic
 * callback function,get activity instance by context,task priority；
 */


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mailssenger.CommonApplication;
import com.mailssenger.LogicObject;
import com.mailssenger.Task;



import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MainService extends Service implements Runnable {

	public static List<Task> allTask = new ArrayList<Task>();
	public static List<Object> allInstance =new ArrayList<Object>();
	public boolean isRun = true;
	private Thread mThread;
	
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mThread = new Thread(this);
		mThread.start();
	}
	
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		isRun=true;
		CommonApplication.debug("Main Service has been started.");
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		isRun=false;														//关闭线程的loop
		mThread.interrupt();												
		mThread=null;
		CommonApplication.debug("Main Service has been stoped.");
	}

	public static void newTask(Task task) {
		
		if (!allTask.contains(task)) {					
			
			allTask.add(task);
			CommonApplication.debug("add task");
		}
	}

	//do the task
	public void doTask(Task task) {
		System.out.println("#####----doTask-----");
		
		Message mess = handler.obtainMessage();
		Map out =new HashMap();

		try {
			Object owner =getInstance(task.getRunClass());														//取得想要运行的类实例;

			if (owner==null)
			{
				owner=task.getRunClass().newInstance();
				allInstance.add(owner);																							//添加到静态实例表中；
			}
			
			Object obj=invokeMethod(owner,task.getRunMethod(),task.getParams());			//反射调用函数；
			out.put("postObject", obj);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("LogicServiceErr","class or,method or params not match!");							////反射失败，可能类名或函数名、参数有问题
		}
		
		out.put("methodName", task.getRunMethod());	
		out.put("activityContext", task.getContext());	
		mess.obj=out;
		handler.sendMessage(mess); // send msg
		allTask.remove(task);      // task done,remove the task
		
	}

	/*get instance from instance table*/
	public Object getInstance(Class cls) {
		
		Iterator it =allInstance.iterator();
		while (it.hasNext()) {
			Object instance=it.next();

				if (cls.isInstance(instance)) 
				{
					return instance;
				}
		}
	     return null;
	}

	/*invoke method of Object ,cannot invoke static method*/
	public Object invokeMethod(Object owner, String methodName, Object[] args) throws Exception {
		 
	     Class ownerClass = owner.getClass();
	     Class[] argsClass = new Class[args.length];
	 
	     for (int i = 0, j = args.length; i < j; i++) {
	         argsClass[i] = args[i].getClass();
	     }

	      Method method = ownerClass.getMethod(methodName,argsClass);
	      method.setAccessible(true);//forbid the security check
	     return method.invoke(owner, args);
	}
	
	
	//do the task all the time
	public void run() {
		// TODO Auto-generated method stub
		while (isRun) {
			//System.out.println("------RUN-----------");
			Task lastTask = null;
			Iterator<Task> ite = allTask.iterator();
			if(ite.hasNext()){
				lastTask = ite.next();
				while(ite.hasNext()){
					Task nextTask = ite.next();
					if (lastTask==null) lastTask=nextTask;
					if(lastTask.getPriority() < nextTask.getPriority())
						lastTask = nextTask;
				}
				doTask(lastTask);
			}
			try{Thread.sleep(100);}catch(Exception e){};
		}
	}


	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//更新UI
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			System.out.println("~~~~Handler~~~~~~~~~");
		
			//the user maybe already exit the activity that request the
			//network connection, so need to catch the error
			try {
				
				Context mContext=(Context)((Map)msg.obj).get("activityContext");
				LogicObject ia = (LogicObject)mContext;
				
				Object postObject = ((Map)msg.obj).get("postObject");
				
				// improve robust, if nework error, toast it but still run the refresh
				//if (postObject.equals("networderr")) 		
					//UIHelper.showToast(mContext, getString(R.string.msg_http_err), SpecialToast.ERRORTOAST, 1000);
				if (postObject.equals("networderr")) 		
					Toast(mContext, "hahahah", 1000);
				
				ia.refresh(((Map)msg.obj).get("methodName"),postObject);
			} catch (Exception e) {
				// TODO: handle exception
				CommonApplication.debug(">>The activity which handle the function of refresh has been close.");
			}

		}

		private void Toast(Context mContext, String string, int i) {
			// TODO Auto-generated method stub
			
		}
		
	};

}
