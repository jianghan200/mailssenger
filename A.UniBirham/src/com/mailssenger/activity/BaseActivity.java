package com.mailssenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.mailssenger.CommonApplication;
import com.mailssenger.service.MainService;
import com.umeng.analytics.MobclickAgent;

/*
 * Base activity, actually is only for debugging,
 * show the menu and then can call the function we want
 */
public class BaseActivity extends ActionBarActivity {

	public BaseActivity context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		context = this;
		
		//put the activity intto activity manager
		CommonApplication.activityManager.putActivity(this);
		
		//Set the home button as clickable
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Start the main service for task
		Intent it = new Intent();
		it.setClass(this, MainService.class);
		startService(it);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// remove activoty from the activity manager
		CommonApplication.activityManager.removeActivity(this);
	}

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

}
