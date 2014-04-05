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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {
	private WelcomeActivity context = null;
	
	private final int SPLASH_DISPLAY_LENGHT = 2100; // The time interval
	private SharedPreferencesUtil mSpUtil;
	
	boolean is_login = false;
	boolean is_debug = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;

		mSpUtil = CommonApplication.getInstance().getSpUtil();
		is_login = mSpUtil.isLogin();
		
		// Make it full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.welcome);
	
		if(!is_debug){
			new Handler().postDelayed(new Runnable() {

				public void run() {
					
					if (is_login) {
						//redirect to   MainListActivity is login is true
						overridePendingTransition(R.anim.enter_alpha,
								R.anim.exit_alpha);
						
//						Intent mainIntent = new Intent(SplashActivity.this,
//								 MainListActivity.class);
						Intent mainIntent = new Intent(WelcomeActivity.this,
								LoginActivity.class);
						WelcomeActivity.this.startActivity(mainIntent);
						WelcomeActivity.this.finish();
						
					} else {

						UIHelper.showMainActivity(context, true);
				
					}

				}

			}, SPLASH_DISPLAY_LENGHT);
		}
	
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "主页面");
		menu.add(0, 1, 0, "1");
		menu.add(0, 2, 0, "1");

		menu.add(0, 3, 0, "1");
		menu.add(0, 4, 0, "1");
		menu.add(0, 5, 0, "1");
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case 0: 
				UIHelper.showMainActivity(context, true);		
				break;
			case 1:
	//			UIHelper.showLibraryActivity(context);
				break;
			case 2:
				
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Umeng SDK
	 * this is for statistic record and analysis
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}