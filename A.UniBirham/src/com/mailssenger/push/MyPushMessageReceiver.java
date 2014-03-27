package com.mailssenger.push;

import com.mailssenger.R;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.activity.MainActivity;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.model.MessageModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.util.L;
import com.mailssenger.util.NetUtil;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.SendMsgAsyncTask;
import com.mailssenger.util.T;



@SuppressLint("NewApi")
public class MyPushMessageReceiver extends BroadcastReceiver {
	
	CommonApplication mApplication ;
	SharedPreferencesUtil mSpUtil;
	Gson mGson;
	UserDB mUserDB;
	MessageDB mMsgDB;
	RecentDB mRecentDB;
	
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
	
	public static final int NOTIFY_ID = 0x000;
	
	public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，
	
	public static final String RESPONSE = "response";
	
	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

	public static abstract interface EventHandler {
		
		public abstract void onChatMessage(MessageModel chatMessage);

		public abstract void onBind(String method, int errorCode, String content);

		public abstract void onNotify(String title, String content);

		public abstract void onNetChange(boolean isNetConnected);

		public void onNewFriend(UserModel u);
	}

	// 接收信息 处理
	@Override
	public void onReceive(Context context, Intent intent) {
		
//		获得基本的管理工具
		mApplication = CommonApplication.getInstance();
		mSpUtil = mApplication.getSpUtil();
		mGson = mApplication.getGson();
		mUserDB = mApplication.getUserDB();
		mMsgDB = mApplication.getMessageDB();
		mRecentDB = mApplication.getRecentDB();
		
		// L.d(TAG, ">>> Receive intent: \r\n" + intent);
		L.i("listener num = " + ehList.size());
		
		//如果是消息
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			
			//获取消息
			String chatMessage = intent.getExtras().getString(
					PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			
			//消息的用户自定义内容读取方式
			//收到消息,自己解析读取
			L.i("onChatMessage: " + chatMessage);
			try {
				
				//将json 消息转换成类
				MessageModel msgItem = CommonApplication.getInstance().getGson()
						.fromJson(chatMessage, MessageModel.class);
				
				//对消息进行预处理
				parseChatMessage(msgItem);// 预处理，过滤一些消息，比如说新人问候或自己发送的
				
			} catch (Exception e) {
				// TODO: handle exception
			}

		
		}else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			// 处理绑定等方法的返回数据
			// PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到
			// 获取方法
			final String method = intent
					.getStringExtra(PushConstants.EXTRA_METHOD);
			// 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			// 绑定失败的原因有多种，如网络原因，或access token过期。
			// 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			// 可以通过限制重试次数，或者在其他时机重新调用来解决。
			final int errorCode = intent
					.getIntExtra(PushConstants.EXTRA_ERROR_CODE,
							PushConstants.ERROR_SUCCESS);
			// 返回内容
			final String content = new String(
					intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));

			// 用户在此自定义处理消息,以下代码为demo界面展示用
			L.i("onChatMessage: method : " + method + ", result : " + errorCode
					+ ", content : " + content);
			paraseContent(context, errorCode, content);// 处理消息

			// 回调函数
			for (int i = 0; i < ehList.size(); i++)
				((EventHandler) ehList.get(i)).onBind(method, errorCode,
						content);

				
		} else if (intent.getAction().equals(
				PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			
			// 可选。通知用户点击事件处理
			L.d(TAG, "intent=" + intent.toUri(0));
			
			//获得标题
			String title = intent
					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			//获得内容
			String content = intent
					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
			//将事件散发出去
			for (int i = 0; i < ehList.size(); i++)
				((EventHandler) ehList.get(i)).onNotify(title, content);
		
		
		} else if (intent.getAction().equals(
				"android.net.conn.CONNECTIVITY_CHANGE")) {
			//如果是网络状态变化
			boolean isNetConnected = NetUtil.isNetConnected(context);
			for (int i = 0; i < ehList.size(); i++)
				((EventHandler) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	//自定义消息预处理
	private void parseChatMessage(MessageModel msg) {
		
	
		// ChatMessage msg = gson.fromJson(chatMessage, ChatMessage.class);
		L.i("gson ====" + msg.toString());
				
		String myEmail = mSpUtil.getEmail();
		String fromEmail = msg.getEmail();//来自谁的信息
		
		int msgType = msg.getMsgType();//消息类型
		String hisMessage = msg.getMessage();
				
		
		if(msgType<10){//普通消息
			
			if (CommonApplication.getInstance().getSpUtil().getMsgSound())// 如果用户开启播放声音
				CommonApplication.getInstance().getMediaPlayer().start();
			if (ehList.size() > 0) {// 有监听的时候，传递下去

				for (int i = 0; i < ehList.size(); i++)
					((EventHandler) ehList.get(i)).onChatMessage(msg);
			} else {
				//没有监听的时候,通知栏提醒，保存数据库
				showNotify(msg);
				
				UserModel u = mUserDB.selectInfo(fromEmail);
				//保存在与他的对话表中
				CommonApplication.getInstance().getMessageDB()
						.saveMsg(u.getEmail(),msg);
				
				//保存到最近会话
				ConversationModel recentItem = new ConversationModel(u.getEmail(), u.getAvatar(),
						u.getNickName(), msg.getMessage(), 0,
						System.currentTimeMillis());
				CommonApplication.getInstance().getRecentDB()
						.saveRecent(recentItem);
			}
		}
		if(msgType==MessageModel.MESSAGE_TYPE_FRIEND_REQUEST){
			
		}
		
		if(msgType==MessageModel.MESSAGE_TYPE_NEW_USER){
			//如果收到的是新用户的消息,此用户必为PUSH服务用户
			
			//新用户如果是自己就不用管了
			if (fromEmail.equals(myEmail))
				return;

			//存下ta的user model 
			UserModel hisUserModel = mGson.fromJson(hisMessage, UserModel.class);
			String hisUserId = hisUserModel.getUserId();
			mUserDB.addUser(hisUserModel);
			
			//将我的user model 发给他
			UserModel myUserModel = mUserDB.selectInfo(myEmail);
			MessageModel msgItem = new MessageModel(mSpUtil.getEmail(),
					MessageModel.MESSAGE_TYPE_NEW_USER_RESPONSE, mGson.toJson(myUserModel) ,System.currentTimeMillis());
			L.e("Here is myUserModel");
			L.e(mGson.toJson(myUserModel));
			//push 消息给他
			new SendMsgAsyncTask(mGson.toJson(msgItem), hisUserId).send();// 同时也回一条消息给对方1
			
			for (EventHandler handler : ehList)
				handler.onNewFriend(hisUserModel);

		}
		if(msgType==MessageModel.MESSAGE_TYPE_NEW_USER_RESPONSE){
			
			Log.e("Response","I am Model");
			UserModel hisUserModel = mGson.fromJson(hisMessage, UserModel.class);
			System.out.println(hisUserModel);
			System.out.println(hisUserModel.getEmail());
			System.out.println(hisUserModel.getUserId());
			//如果这个用户还没有,就将它存在自己的数据库中
			if(mUserDB.selectInfo(hisUserModel.getEmail())==null){
				mUserDB.addUser(hisUserModel);
			}
			
			
		}

	}

	@SuppressWarnings("deprecation")
	private void showNotify(MessageModel chatMessage) {
		//新消息数自增
		mNewNum++;
		
		// 更新通知栏
		CommonApplication mApplication = CommonApplication.getInstance();

		int icon = R.drawable.notify_newmessage;
		
		CharSequence tickerText = chatMessage.getEmail() + ":"
				+ chatMessage.getMessage();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		notification.flags = Notification.FLAG_NO_CLEAR;
		// 设置默认声音
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// 设定震动(需加VIBRATE权限)
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.contentView = null;

		Intent intent = new Intent(mApplication, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mApplication, 0,
				intent, 0);
		notification.setLatestEventInfo(CommonApplication.getInstance(),
				mApplication.getSpUtil().getNick() + " (" + mNewNum + "new message)",
				tickerText, contentIntent);

		mApplication.getNotificationManager().notify(NOTIFY_ID, notification);// 通知一下才会生效哦
	}

	/**
	 * 处理登录结果
	 * 
	 * @param errorCode
	 * @param content
	 */
	private void paraseContent(final Context context, int errorCode,
			String content) {
		// TODO Auto-generated method stub
		if (errorCode == 0) {
			String appid = "";
			String channelid = "";
			String userid = "";

			try {
				JSONObject jsonContent = new JSONObject(content);
				JSONObject params = jsonContent
						.getJSONObject("response_params");
				appid = params.getString("appid");
				channelid = params.getString("channel_id");
				userid = params.getString("user_id");
			} catch (JSONException e) {
				L.e(TAG, "Parse bind json infos error: " + e);
			}
			
			SharedPreferencesUtil mSpUtil = CommonApplication.getInstance()
					.getSpUtil();
			
			//当为第一次注册的时候
			if(mSpUtil.getAppId()==""){
				
				mSpUtil.setAppId(appid);
				mSpUtil.setChannelId(channelid);
				mSpUtil.setUserId(userid);
				
				// 新注册给大家发布一个新人通知
				UserModel myUserModel = new UserModel();
				myUserModel.setEmail(mSpUtil.getEmail());
				myUserModel.setChannelId(channelid);
				myUserModel.setUserId(userid);
				myUserModel.setNickName(mSpUtil.getNick());
				
				//并且把自己添加到数据库
				//自己绑定成功
				//把自己添加到数据库
				mUserDB.addUser(myUserModel);
				
				Gson mGson = CommonApplication.getInstance().getGson();
				
				MessageModel msgItem = new MessageModel(mSpUtil.getEmail(),
						MessageModel.MESSAGE_TYPE_NEW_USER, mGson.toJson(myUserModel),System.currentTimeMillis());
				
				new SendMsgAsyncTask(mGson.toJson(msgItem), "").send();
				
				//创建一条问候自己的消息
				MessageModel chatMessage = new MessageModel(mSpUtil.getEmail(),
						MessageModel.MESSAGE_TYPE_TEXT, "Hello, I am you~",System.currentTimeMillis());
				
				mMsgDB.saveMsg(myUserModel.getEmail(), chatMessage);
				
			}
			
		} else {
			if (NetUtil.isNetConnected(context)) {
				if (errorCode == 30607) {
					T.showLong(context, "账号已过期，请重新登录");
					// 跳转到重新登录的界面
				} else {
					T.showLong(context, "启动失败，正在重试...");
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							PushManager.startWork(context,
									PushConstants.LOGIN_TYPE_API_KEY,
									CommonApplication.API_KEY);
						}
					}, 2000);// 两秒后重新开始验证
				}
			} else {
				T.showLong(context, R.string.net_error_tip);
			}
		}
	}
}
