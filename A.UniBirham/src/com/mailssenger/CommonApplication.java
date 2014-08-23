package com.mailssenger;

import com.mailssenger.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.baidu.frontia.FrontiaApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mailssenger.activity.MainActivity;
import com.mailssenger.db.MailDB;
import com.mailssenger.db.MsgDB;
import com.mailssenger.db.ConvDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.UserModel;
import com.mailssenger.push.BaiduPush;
import com.mailssenger.push.MyPushMessageReceiver;
import com.mailssenger.service.MainService;
import com.mailssenger.service.NotificationService;
import com.mailssenger.util.SharedPreferencesUtil;



/**
 * This is the entrance of the application
 * Global functions and variance is kept here
 */
public class CommonApplication extends Application {

	//百度云推送 我的teamhub上面的值
	public final static String API_KEY = "ekONpN2M5jT1aZXt3V3vTysl";
	public final static String SECRIT_KEY = "iWPNBQpDKWWGNg3TyYE6xleVOg8nNuym";
	
	//文件名字
	public static final String SP_FILE_NAME = "mailssenger_sp";
	public static final String DB_NAME = "mailssenger_db";
	
	//表情页设置
	public static final int NUM_PAGE = 6;// 总共有多少页
	public static int NUM = 20;// 每页20个表情,还有最后一个删除button
	
	// Global data
	public static String ACCOUNT = "";
	public static String PASSWORD = "";
	public static String SACCOUNT = ""; // Account for authentication

	public static String IMAP_HOST = "";
	public static String SMTP_HOST = "";
	
	public static String SERVER_URL = "http://www.hello.com/";
	public static String OAUTH_TOKEN = "http://www.hello.com/";

	//用户邮件UID集合,用于接收新邮件
	public static Set<Integer> UID_SET = null;

	// head image
	public static final int[] heads = { R.drawable.h0, R.drawable.h1,
			R.drawable.h2, R.drawable.h3, R.drawable.h4, R.drawable.h5,
			R.drawable.h6, R.drawable.h7, R.drawable.h8, R.drawable.h9,
			R.drawable.h10, R.drawable.h11, R.drawable.h12, R.drawable.h13,
			R.drawable.h14, R.drawable.h15, R.drawable.h16, R.drawable.h17,
			R.drawable.h18 };
	
	private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();

	private static CommonApplication context;
	public static ActivityManager activityManager;
	
	private BaiduPush mBaiduPushServer;
	private SharedPreferencesUtil mSpUtil;
	
	public static MailDB dbOperation;
	private UserDB mUserDB;
	private MsgDB mMsgDB;
	private ConvDB mConvDB;
	private MediaPlayer mMediaPlayer;

	private List<UserModel> mUserList;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private Gson mGson;
	

	private Handler handler;
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * the entrance for the application
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;

		// Baidu Push Service
		FrontiaApplication.initFrontiaApplication(context);

		mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
				SECRIT_KEY, API_KEY);
		
		
		// 不转换没有 @Expose 注解的字段
		mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		
		mSpUtil = new SharedPreferencesUtil(this, SP_FILE_NAME);
		mUserDB = new UserDB(this);
		mMsgDB = new MsgDB(this);
		mConvDB = new ConvDB(this);
		mUserList = mUserDB.getAll();
		mMediaPlayer = MediaPlayer.create(this, R.raw.office);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		loadUserBaseData();

		activityManager = ActivityManager.getActivityManager(this); // 实例化活动管理类
		dbOperation = new MailDB(this);
		UID_SET = dbOperation.loadUIDSet("inbox");
		
//		// Start Notification Service
//		startNotificationService();	
	}

	public synchronized static CommonApplication getInstance() {
		return context;
	}

	/**
	 * Read user data
	 */
	public void loadUserBaseData() {
		CommonApplication.ACCOUNT = mSpUtil.getEmail();
		CommonApplication.PASSWORD = mSpUtil.getPassword();
		CommonApplication.SACCOUNT = mSpUtil.getMailServerAccount();
		CommonApplication.IMAP_HOST = mSpUtil.getIMAPHost();
		CommonApplication.SMTP_HOST = mSpUtil.getSMTPHost();
	}

	/**
	 * Save user data in Share Preferences
	 * 
	 * @param
	 */
	public void saveUserBaseData() {	
		mSpUtil.setIMAPHost(CommonApplication.IMAP_HOST);
		mSpUtil.setSMTPHost(CommonApplication.SMTP_HOST);
		mSpUtil.setMailServerAccount(CommonApplication.SACCOUNT);
	}

	/**
	 * Check the network
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * Stop the notification service
	 */
	public void stopNotificationService() {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent mAlarmSender = PendingIntent.getService(context, 0,
				new Intent(context, NotificationService.class), 0);
		am.cancel(mAlarmSender);
	}

	/**
	 * see whether the user already add the account
	 */
	public static boolean isLogin() {
		// if account is not set ,that means the usr has not login yet
		if (ACCOUNT != null && !ACCOUNT.equals(""))
			return true;
		return false;
	}

	/**
	 * print out the debug info
	 */
	public static void debug(String debugString) {
		// When release the statement below
		System.out.println(">>>debug>>>" + debugString);
	}

	/*
	 * The exit of the applictaion(non-Javadoc)
	 * 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();

		saveUserBaseData();

		// stop the main service
		Intent it = new Intent();
		it.setClass(this, MainService.class);
		stopService(it);

		// stop the notification service
		stopNotificationService();

		activityManager.exit();
	}

	public synchronized BaiduPush getBaiduPush() {
		if (mBaiduPushServer == null)
			mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
					SECRIT_KEY, API_KEY);
		return mBaiduPushServer;

	}

	public synchronized Gson getGson() {
		if (mGson == null)
			// 不转换没有 @Expose 注解的字段
			mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.create();
		return mGson;
	}

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.office);
		return mMediaPlayer;
	}

	public synchronized UserDB getUserDB() {
		if (mUserDB == null)
			mUserDB = new UserDB(this);
		return mUserDB;
	}

	public synchronized ConvDB getConvDB() {
		if (mConvDB == null)
			mConvDB = new ConvDB(this);
		return mConvDB;
	}

	public synchronized MsgDB getMsgDB() {
		if (mMsgDB == null)
			mMsgDB = new MsgDB(this);
		return mMsgDB;
	}

	public synchronized List<UserModel> getUserList() {
		if (mUserList == null)
			mUserList = getUserDB().getAll();
		return mUserList;
	}

	public synchronized SharedPreferencesUtil getSpUtil() {
		if (mSpUtil == null)
			mSpUtil = new SharedPreferencesUtil(this, SP_FILE_NAME);
		return mSpUtil;
	}

	public Map<String, Integer> getFaceMap() {
		if (!mFaceMap.isEmpty())
			return mFaceMap;
		return null;
	}

	
	/**
	 * 创建挂机图标
	 */
	@SuppressWarnings("deprecation")
	public void showNotification() {
		if (!mSpUtil.getMsgNotify())// 如果用户设置不显示挂机图标，直接返回
			return;

		// int icon = R.drawable.notify_general;
		int icon = R.drawable.app_icon;

		// CharSequence tickerText = getResources().getString(
		// R.string.app_is_run_background);
		CharSequence tickerText = "App is running in the background";

		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notify_status_bar_latest_event_view);

		contentView.setImageViewResource(R.id.icon, heads[2]);

		contentView.setTextViewText(R.id.title, mSpUtil.getNick());
		contentView.setTextViewText(R.id.text, tickerText);
		contentView.setLong(R.id.time, "setTime", when);
		// 指定个性化视图
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定内容意图
		mNotification.contentIntent = contentIntent;
		// 下面是4.0notify
		// Bitmap icon = BitmapFactory.decodeResource(getResources(),
		// heads[mSpUtil.getHeadIcon()]);
		// Notification.Builder notificationBuilder = new Notification.Builder(
		// this).setContentTitle(mSpUtil.getNick())
		// .setContentText(tickerText)
		// .setSmallIcon(R.drawable.notify_general)
		// .setWhen(System.currentTimeMillis())
		// .setContentIntent(contentIntent).setLargeIcon(icon);
		// Notification n = notificationBuilder.getNotification();
		// n.flags |= Notification.FLAG_NO_CLEAR;

		mNotificationManager.notify(MyPushMessageReceiver.NOTIFY_ID,
				mNotification);
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
