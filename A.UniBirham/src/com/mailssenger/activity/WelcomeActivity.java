package com.mailssenger.activity;

import com.mailssenger.CommonApplication;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import com.mailssenger.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {
	private WelcomeActivity context = null;
	private final int SPLASH_DISPLAY_LENGHT = 2100; // The time interval
	private SharedPreferencesUtil mSpUtil;
	
	boolean is_login = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		mSpUtil = CommonApplication.getInstance().getSpUtil();
		
		// Make it full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.splash);
	

		is_login = mSpUtil.isLogin();
		new Handler().postDelayed(new Runnable() {

			public void run() {
				
				if (is_login) {
					//redirect to   MainListActivity is login is true
					overridePendingTransition(R.anim.enter_alpha,
							R.anim.exit_alpha);
//					
//					Intent mainIntent = new Intent(SplashActivity.this,
//							 MainListActivity.class);
					Intent mainIntent = new Intent(WelcomeActivity.this,
							LoginActivity.class);
					WelcomeActivity.this.startActivity(mainIntent);
					WelcomeActivity.this.finish();
					
				} else {
					
					
					//show SplashActivity is login is true
					Intent mainIntent = new Intent(WelcomeActivity.this,
							LoginActivity.class);
					WelcomeActivity.this.startActivity(mainIntent);
					WelcomeActivity.this.finish();
				}

			}

		}, SPLASH_DISPLAY_LENGHT);
	}

	// Umeng SDK
	// this is for statistic record and analysis
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}