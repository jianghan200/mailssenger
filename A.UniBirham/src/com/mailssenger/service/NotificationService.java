/**
 * Notification Service
 * @author Han
 */
package com.mailssenger.service;

import com.mailssenger.R;

import java.util.Set;

import com.mailssenger.CommonApplication;
import com.mailssenger.LogicObject;
import com.mailssenger.Task;
import com.mailssenger.activity.MainActivity;
import com.mailssenger.mail.MailAccount;
import com.mailssenger.util.TaskHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

//Notification Service
public class NotificationService extends Service implements LogicObject {
	private NotificationService context = this;
	private static Integer notificationCount = 0;

	private static Task task = new Task();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CommonApplication.debug("NotificationService has been started.");
	}

	/* Get the new mail number when start */
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		CommonApplication.debug("NotificationService::onStart");
		
		if (CommonApplication.ACCOUNT != null
				&& !CommonApplication.ACCOUNT.equals("")) {
			CommonApplication
					.debug("NotificationService::onStart::start the service!");

			task.setContext(this);
			task.setPriority(0);
			// the priority is the lowest(not to disturb the user)
			task.setMethod(MailAccount.class, "getNewUnreadMailSet", "inbox");
			MainService.newTask(task);
			// new Thread(checkAndNotify).start();

		}

	}

	// call back method, show the notification
	public void refresh(Object... args) {
		// TODO Auto-generated method stub
		CommonApplication.debug("NotificationService is refreshing");

		if (args[0] == "getNewUnreadMailSet") {
			try {

				CommonApplication.debug("This is by getNewUnreadMailSet");
				// see whether the unread number is change
				Set<Integer> unread_uid_set = (Set<Integer>) args[1];
				int count = unread_uid_set.size();

				// whether there is no new email
				if (count == 0) {
					cancelNotifictaion();
					return;
				}
				notificationCount = count;

				TaskHelper.taskCheckNewMail(this, "inbox", 6);

				// show the notification

				notifyNewMail(notificationCount);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * runnabke for check and notify
	 */
	Runnable checkAndNotify = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {

				CommonApplication.debug("This is by checkAndNotify");
				MailAccount mailAcc = new MailAccount();
				Set<Integer> unread_uid_set = mailAcc
						.getNewUnreadMailSet("inbox");
				int count = unread_uid_set.size();

				// whether there is no new email
				if (count == 0) {
					cancelNotifictaion();
					return;
				}
				notificationCount = count;
				mailAcc.checkNewMailFromServer("inbox");
				notifyNewMail(notificationCount);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * notify new mail
	 */
	private void notifyNewMail(int notificationCount) {

		// get the notification manager
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// construct a new notification
		Notification notification = new Notification(R.drawable.app_icon,
				"New mail~", System.currentTimeMillis());
		// set the action when click the notifictaion
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		notification.setLatestEventInfo(getApplicationContext(), "Reminder",
				"you got" + notificationCount + " new mail!", pendingIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		manager.notify(393, notification);// start the notification
	}

	/*
	 * cancel notifictaion
	 */
	public void cancelNotifictaion() {
		// get the notification manager
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		manager.cancel(393);
	}

}
