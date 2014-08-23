/**
 * operation related to UI,such as activity switch
 */
package com.mailssenger.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.mailssenger.activity.LoginActivity;
import com.mailssenger.activity.MainActivity;
import com.mailssenger.activity.MainActivityDrawer;
import com.mailssenger.activity.SendMailActivity;
import com.mailssenger.activity.WelcomeActivity;

public class UIHelper {
	public final static int BACK = 1;
	public final static int LEFT_MENU = 2;
	public final static int SEARCH = 3;
	public final static int OK = 4;
	public final static int IMAGE = 5;
	public final static int REFRESH = 6;
	public final static int SYNC=7;
	
	public final static int UPLOAD = 7;
	public final static int DOWNLOAD = 8;
	
	
	//just helper for  show other activity
//	public static void showMainListActivity(Context context) {
//		Intent intent = new Intent(context, MainListActivity.class);
//		context.startActivity(intent);
//	}
	
	public static void showMainActivity(Context context, boolean finish) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
		if(finish){
			((Activity) context).finish();
		}
	}
	
	public static void showMainActivityDrawer(Context context, boolean finish) {
		Intent intent = new Intent(context, MainActivityDrawer.class);
		context.startActivity(intent);
		if(finish){
			((Activity) context).finish();
		}
	}
	
	
	public static void showWelcomeActivity(Context context, boolean finish) {
		Intent intent = new Intent(context, WelcomeActivity.class);
		context.startActivity(intent);
		if(finish){
			((Activity) context).finish();
		}
	}
	
	public static void showLoginActivity(Context context, boolean finish) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
		if(finish){
			((Activity) context).finish();
		}
	}
	
	public static void showSendMailActivity(Context context, boolean finish) {
		Intent intent = new Intent(context, SendMailActivity.class);
		context.startActivity(intent);
		if(finish){
			((Activity) context).finish();
		}
	}
	
	


//	public static void 	toastNoNewMail(){
//		Message msg = new Message();
//		Bundle bundle = new Bundle();
//		bundle.putString("sign", "noNewMail"); //save value in bundle
//		msg.setData(bundle);// msg using bundle to pass value
//		MainListActivity.handler.sendMessage(msg);
//	}
//	
	
//	public static void 	updateMainListAvtivityView(){
//		Message msg = new Message();
//		Bundle bundle = new Bundle();
//		bundle.putString("sign", "updateUI"); //save value in bundle
//		msg.setData(bundle);// msg using bundle to pass value
//		MainListActivity.handler.sendMessage(msg);
//	}

}
