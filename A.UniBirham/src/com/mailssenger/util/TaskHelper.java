package com.mailssenger.util;

import com.mailssenger.Task;
import com.mailssenger.mail.MailAccount;
import com.mailssenger.service.MainService;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class TaskHelper {
	
	//just helper for call the network related task
	
	/*
	 * check new mail task
	 */
	public static void taskCheckNewMail(Context context,String folderName,int priority){
		Task task = new Task(context, priority);
		try {
			task.setMethod(MailAccount.class, "checkNewMailFromServer",
					folderName);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		MainService.newTask(task);
	}
	
	
	/*
	 * send mail task
	 */
	public static void taskSendMail(Context context,Bundle bundle){
		Task task = new Task(context, 2);
		try {
			task.setMethod(MailAccount.class, "sendMail", bundle);

			Toast.makeText(context, "Mail sending task is added!",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(context,
					"Error happens when sending email,please contact the developer",
					Toast.LENGTH_LONG).show();
		}
		MainService.newTask(task);
	}
	
	/*
	 * sync mail task
	 */
	public static void taskSyncMail(Context context,String folderName){
		Task task = new Task(context, 2);
		try {
			task.setMethod(MailAccount.class, "syncMail", folderName);

			Toast.makeText(context, "Mail sync task is added!",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(context,
					"Error happens when sync ,please contact the developer",
					Toast.LENGTH_LONG).show();
		}
		MainService.newTask(task);
	}
	

	
	


}
