package com.mailssenger.activity;

import com.mailssenger.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mailssenger.CommonApplication;
import com.mailssenger.service.MainService;
import com.mailssenger.service.NotificationService;
import com.umeng.analytics.MobclickAgent;

/*
 * Base activity, actually is only for debugging,
 * show the menu and then can call the function we want
 */
public class BaseActivity extends ActionBarActivity {

	public BaseActivity context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		CommonApplication.activityManager.putActivity(this);

		// Start the main service for task
		Intent it = new Intent();
		it.setClass(this, MainService.class);
		startService(it);

//		// Start Notification Service
//		startNotificationService();

		// Auto Check Update
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// remove activoty from the activity manager
		CommonApplication.activityManager.removeActivity(this);
	}

	/*
	 * Umeng SDK for (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	
	/**
	 * Start the notification service
	 * call the notification every 5 mins
	 */
	public void startNotificationService() {
		
//		Intent it = new Intent();
//		it.setClass(this, NotificationService.class);
//		startService(it);
		
		CommonApplication.debug("I am startNotificationService");
		// set the alarm to run periodly
		PendingIntent mAlarmSender = PendingIntent.getService(
				getApplicationContext(), 0, new Intent(getApplicationContext(),
						NotificationService.class), 0);

		CommonApplication.debug("I am starting the ns!");
		long firstTime = SystemClock.elapsedRealtime();
		
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		// this code is unvaild under xiaomi phone
		// am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
		// 1 * 1000, mAlarmSender);
		
		// make time related to system time so that can wake up from sleep
		am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, 2*60 * 1000,
				mAlarmSender);
		// check every three minutes!

	}
	

}
