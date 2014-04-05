package com.mailssenger.activity;

import java.util.LinkedList;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.service.MainService;
import com.mailssenger.util.SharedPreferencesUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

/*
 * Base activity, actually is only for debugging,
 * show the menu and then can call the function we want
 */
public class BaseActivity extends ActionBarActivity {

	public BaseActivity context = null;
	
	//初始化工具
	protected CommonApplication mApplication;
	protected SharedPreferencesUtil mSpUtil;
	protected UserDB mUserDB;
	protected RecentDB mRecentDB;
	protected MessageDB mMsgDB;
	protected MediaPlayer mMediaPlayer;
	protected Gson mGson;

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
		
		//初始化各工具
		mApplication = CommonApplication.getInstance();
		mSpUtil = mApplication.getSpUtil();
		mGson = mApplication.getGson();
		mUserDB = mApplication.getUserDB();
		mMsgDB = mApplication.getMessageDB();
		mRecentDB = mApplication.getRecentDB();
				
		//友盟强制升级
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
		    @Override
		    public void onClick(int status) {
		        switch (status) {
		        case UpdateStatus.Update:
		            break;
		        default:
		            //close the app
		        	context.finish();
		        }
		    }
		});
		
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
