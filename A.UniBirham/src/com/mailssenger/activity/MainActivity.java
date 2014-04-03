package com.mailssenger.activity;


import java.util.LinkedList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.LogicObject;
import com.mailssenger.R;
import com.mailssenger.Task;
import com.mailssenger.adapter.RecentAdapter;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.fragment.ConvFragment;
import com.mailssenger.fragment.RightFragment;
import com.mailssenger.fragment.SlidingMenuLeft;
import com.mailssenger.mail.MailAccount;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.model.MessageModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.push.MyPushMessageReceiver;
import com.mailssenger.search.MySearchableActivity;
import com.mailssenger.service.MainService;
import com.mailssenger.slidinglayer.SlidingLayer;
import com.mailssenger.util.L;
import com.mailssenger.util.NetUtil;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.T;
import com.slidingmenu.lib.SlidingMenu;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class MainActivity extends BaseActivity implements OnClickListener,
MyPushMessageReceiver.EventHandler, LogicObject, SearchView.OnQueryTextListener,PopupMenu.OnMenuItemClickListener{
	
	//添加LogicObject之后才可以添加任务
	private static String TAG = " >MainActivity";
	private MainActivity context;//hello
	
	ConvFragment convFragment = null;
//	GroupsFragment groupsFragment;
//	ContactsFragment contactsFragment;
	
	LinkedList<ConversationModel> mRecentDatas;
	public RecentAdapter mRecentAdapter;
	
	//sliding menu
	public static SlidingMenu slidingMenu;
	
	int mScreenWidth;
	
	//初始化工具
	private CommonApplication mApplication;
	private SharedPreferencesUtil mSpUtil;
	private UserDB mUserDB;
	private RecentDB mRecentDB;
	private MessageDB mMsgDB;
	private MediaPlayer mMediaPlayer;
	private Gson mGson;
		
	//网络提醒
	private View mNetErrorView;

	//邮件功能
	private static String folder = "inbox";
	
	//
	RightFragment mFragRight; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		setContentView(R.layout.activity_main);
		
		setTitle("Mailssenger");
		

		//初始化各工具
		mApplication = CommonApplication.getInstance();
		mSpUtil = mApplication.getSpUtil();
		mGson = mApplication.getGson();
		mUserDB = mApplication.getUserDB();
		mMsgDB = mApplication.getMessageDB();
		mRecentDB = mApplication.getRecentDB();
		mApplication.setHandler(handler);
		mRecentDatas = new LinkedList<ConversationModel>();
		
		mRecentDatas = mRecentDB.getRecentList();
		mRecentAdapter = new RecentAdapter(this);
		mRecentAdapter.setData(mRecentDatas);
	
		//start baidu push
		PushManager.startWork(getApplicationContext(),
		PushConstants.LOGIN_TYPE_API_KEY, CommonApplication.API_KEY);
		
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


		//初始化网络提示
		mNetErrorView = findViewById(R.id.net_status_bar_top);
		mNetErrorView.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			startActivity(new Intent(
    					android.provider.Settings.ACTION_WIFI_SETTINGS));
    		}
    	});
		
	
	  	
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.main_frame, new ConvFragment(mRecentAdapter)).commit();
		
    	initSlidingMenu();
    	

		// get some bsaic data
        Task task = new Task(context);
        task.setMethod(MailAccount.class, "getLatestMails","inbox", (Integer)10);
        MainService.newTask(task);
        
     
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MyPushMessageReceiver.ehList.add(this);
		
		L.e(TAG,"I am paused");
		System.out.println(MyPushMessageReceiver.ehList);
		
		if (!PushManager.isPushEnabled(this))
			PushManager.resumeWork(this);

		//网络操作显示
		if (!NetUtil.isNetConnected(this))
			mNetErrorView.setVisibility(View.VISIBLE);
		else {
			mNetErrorView.setVisibility(View.GONE);
		}
		
		mApplication.getNotificationManager().cancel(
				MyPushMessageReceiver.NOTIFY_ID);
		MyPushMessageReceiver.mNewNum = 0;
		
		//update the info
		updateConvFragment();

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyPushMessageReceiver.ehList.remove(this);// 暂停就移除监听
		L.e(TAG,"I am paused");
		System.out.println(MyPushMessageReceiver.ehList);
	}
	

	
	
	
	public void initSlidingMenu(){
		
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
//		// TODO Auto-generated method stub
//		setBehindContentView(R.layout.main_left_layout);// 设置左菜单
//		FragmentTransaction mFragementTransaction = getSupportFragmentManager()
//				.beginTransaction();
//		Fragment mFrag = new LeftFragment();
//		mFragementTransaction.replace(R.id.main_left_fragment, mFrag);
//		mFragementTransaction.commit();
//		// customize the SlidingMenu
//		mSlidingMenu = getSlidingMenu();
//		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);// 设置是左滑还是右滑，还是左右都可以滑，我这里左右都可以滑
//		mSlidingMenu.setShadowWidth(mScreenWidth / 40);// 设置阴影宽度
//		mSlidingMenu.setBehindOffset(mScreenWidth / 8);// 设置菜单宽度
//		mSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例
//		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
//		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);// 设置左菜单阴影图片
//		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);// 设置右菜单阴影图片
//		mSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
//		mSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果
		// set up sliding menu
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		
		slidingMenu = new SlidingMenu(context);
		slidingMenu.setMode(SlidingMenu.LEFT);
		
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setShadowWidth(mScreenWidth / 40);// 设置阴影宽度
		slidingMenu.setBehindOffset(mScreenWidth / 8);// 设置菜单宽度
		
//		slidingMenu.setShadowWidth(0);// 设置阴影宽度
//		slidingMenu.setBehindOffset(0);// 设置菜单宽度
		
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(context, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu_left_layout);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_left_fragment, new SlidingMenuLeft()).commit();

//		slidingMenu.setSecondaryMenu(R.layout.slidingmenu_right_layout);
//		FragmentTransaction mFragementTransaction = getSupportFragmentManager()
//				.beginTransaction();
//		mFragRight = new RightFragment();
//		mFragementTransaction.replace(R.id.main_right_fragment, mFragRight);
//		mFragementTransaction.commit();
		
//		slidingMenu.setSecondaryMenu(R.layout.slidingmenu_right_frame);
//		slidingMenu
//				.setSecondaryShadowDrawable(R.drawable.slidingmenu_shadowright);
//		getSupportFragmentManager().beginTransaction()
//				.replace(R.id.slidingmenu_right, new RightFragment()).commit();
	}
	
		
	public static int UPDATE_CHATS=100;
	public static int UPDATE_GROUPS=101;
	public static int UPDATE_CONTACTS=102;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == UPDATE_CHATS){
				updateChatListView();
			}
			if(msg.what == UPDATE_GROUPS){
				
			}
			if(msg.what == UPDATE_CONTACTS){
				
			}
			
			switch (msg.what) {
				
				
				case MessageModel.MESSAGE_TYPE_NEW_USER:
					
					UserModel u = (UserModel) msg.obj;
					mUserDB.addUser(u);
					T.showShort(mApplication, "好友列表已更新!");
					
					//Demo 数据
					ConversationModel recentItem = new ConversationModel();
					recentItem.setEmail(u.getEmail());
					recentItem.setName(u.getNickName());
					recentItem.setMessage("Hi~ I am"+u.getNickName());
					recentItem.setNewNum(0);
					recentItem.setTime(System.currentTimeMillis());
					mRecentDB.saveRecent(recentItem);
					
					updateConvFragment();
					

					break;
					
				case MessageModel.MESSAGE_TYPE_TEXT:
					
					//设定新消息
					MessageModel msgItem = (MessageModel) msg.obj;
					String email = msgItem.getEmail();
					String content = msgItem.getMessage();
					msgItem.setNew(true);
					msgItem.setCome(true);
					
					//只有存在好友关系,才可以发文本信息,所以受到文本消息的时候,用户必然存在
					UserModel user = mUserDB.selectInfo(email);
					if(user!=null){
						
					}
					
					L.e(TAG,mGson.toJson(msgItem));
					L.e(TAG,"save msg");
					mMsgDB.saveMsg(user.getEmail(), msgItem);
					
					recentItem = new ConversationModel(email, user.getAvatar(), user.getNickName(),
							msgItem.getMessage(), 0, System.currentTimeMillis());
					
					mRecentDB.saveRecent(recentItem);
					
					
					L.e(TAG,"I will be in updateConvFragment()");
					updateConvFragment();
//					mRecentAdapter.addFirst(recentItem);
					T.showShort(mApplication, user.getNickName() + ":" + content);
					break;
				default:
					break;
			}
			
		}
	};



	@Override
	public void onChatMessage(MessageModel chatMessage) {
		//将得到的消息交给handler处理
		Message handlerMsg = handler.obtainMessage(MessageModel.MESSAGE_TYPE_TEXT);
		handlerMsg.obj = chatMessage;
		handler.sendMessage(handlerMsg);
	}


	@Override
	public void onBind(String method, int errorCode, String content) {
		//绑定操作应该直接由Push自己完成
		if (errorCode == 0) {
			L.e(TAG," On　bind");
//			if(mSpUtil.isFirst()){
//				L.e(TAG,"Fisrt Time Bind");
//				
//				//将自己添加到对话列表
////				RecentConversationModel recentItem = new RecentConversationModel();
////				recentItem.setEmail(myUserModel.getEmail());
////				recentItem.setName(myUserModel.getNickName());
////				recentItem.setMessage(chatMessage.getMessage());
////				recentItem.setNewNum(0);
////				recentItem.setTime(System.currentTimeMillis());
////
////				mRecentDB.saveRecent(recentItem);
//				
//				L.e(TAG,"add initial data");
//				initialUserDB();
//				
//				handler.sendEmptyMessage(UniBirhamActivity.UPDATE_CHATS);
//				mSpUtil.setFirst(false);
//			}
			
		}
		

	}


	@Override
	public void onNotify(String title, String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (!isNetConnected) {
			T.showShort(this, R.string.net_error_tip);
			mNetErrorView.setVisibility(View.VISIBLE);
		} else {
			mNetErrorView.setVisibility(View.GONE);
		}
	}


	@Override
	public void onNewFriend(UserModel u) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * rewrite the menu button
	 */
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	
		 if (keyCode == KeyEvent.KEYCODE_MENU) {
			 if (slidingMenu.isMenuShowing()) {
				 slidingMenu.showContent();
			 } else {
				slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
				slidingMenu.showMenu();
				slidingMenu.setBehindOffset(mScreenWidth / 8);// 设置菜单宽度
			 }
			 return false;
			 }
		 return super.onKeyDown(keyCode, event);
		
	 }
	
	 
		/**
		 * 连续按两次返回键就退出
		 */
		private long firstTime;
	/**
	 * rewrite the sliding menu
	 */
	@Override
	public void onBackPressed() {
		//if menu is showing, hide ,else quit
		if (slidingMenu.isMenuShowing()) {
			
			if(mFragRight!=null){
				SlidingLayer mSlidingLayer = mFragRight.getSlidingLayer();
				if(mSlidingLayer != null && mSlidingLayer.isOpened()){
					mSlidingLayer.removeAllViews();
					mSlidingLayer.closeLayer(true);
					return;
				}
			}
			
			slidingMenu.showContent();
		} else {
			
			if (System.currentTimeMillis() - firstTime < 3000) {
				mApplication.showNotification();
				// if (!mSpUtil.getMsgNotify() && PushManager.isPushEnabled(this))
				// PushManager.stopWork(this);
				finish();
			} else {
				firstTime = System.currentTimeMillis();
				if (mSpUtil.getMsgNotify())
					T.showShort(this, R.string.press_again_backrun);
				else
					T.showShort(this, R.string.press_again_exit);
			}

		}
	}
	




	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void refresh(Object... args) {
		// TODO Auto-generated method stub
		
		if (args[0].equals("getLatestMails" )) {
			updateChatListView();
			T.dShowShort(context, "I am updating!");
		}

		
	}
	
	public void updateChatListView(){
//		mRecentDatas = mRecentDB.getRecentList();
//		mRecentAdapter.setData(mRecentDatas);
//		mRecentAdapter.notifyDataSetChanged();
	}
	
    public void initialUserDB(){
   	UserDB mUserDB =CommonApplication.getInstance().getUserDB();
   	RecentDB mRecentDB=CommonApplication.getInstance().getRecentDB();
		
		UserModel u  = new UserModel();
		u.setEmail("1129966399@qq.com");
		u.setNickName("Han Jiang");			
		mUserDB.addUser(u);
		
		u = new UserModel();
		u.setEmail("zxm024@bham.ac.uk");
		u.setNickName("Kenny Ma");			
		mUserDB.addUser(u);
		
		u = new UserModel();
		u.setEmail("HYW399@cs.bham.ac.uk");
		u.setNickName("Bowie");			
		mUserDB.addUser(u);
		
		u = new UserModel();
		u.setEmail("TYL375@cs.bham.ac.uk‎");
		u.setNickName("Issac");			
		mUserDB.addUser(u);
		
		u = new UserModel();
		u.setEmail("YXF373@cs.bham.ac.uk");
		u.setNickName("Seffy");			
		mUserDB.addUser(u);
		
		//Demo 数据
		ConversationModel recentItem = new ConversationModel();
		recentItem.setEmail("zxm024@bham.ac.uk");
		recentItem.setName("Kenny Ma");
		recentItem.setMessage("Here is Kenny");
		recentItem.setNewNum(0);
		recentItem.setTime(System.currentTimeMillis());
		mRecentDB.saveRecent(recentItem);

		recentItem = new ConversationModel();
		recentItem.setEmail("1129966399@qq.com");
		recentItem.setName("Han Jiang");
		recentItem.setMessage("It is a Chinese name");
		recentItem.setNewNum(0);
		recentItem.setTime(System.currentTimeMillis());
		mRecentDB.saveRecent(recentItem);

    }

    public void updateConvFragment(){
//    	L.i(TAG,"I am updateConvFragment");
//    	   if (convFragment == null){
//    		   L.i(TAG,"convFragment == null");
//    		   convFragment = (ConvFragment ) getSupportFragmentManager()
//      					.findFragmentById(R.id.fragment_conv);
//    	   }
//    	   L.i(TAG,"updateRecentListView");
//    	   convFragment.updateRecentListView();
    	mRecentDatas = mRecentDB.getRecentList();
		mRecentAdapter.setData(mRecentDatas);
		mRecentAdapter.notifyDataSetChanged();
    }
    
    
    
	//this for the search
	@Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        return true;
    }
 
    @Override
    public boolean onQueryTextChange(String s) {
    	
        return false;
    }
    
    private SearchView mSearchView;
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	 
//	    MenuItem searchItem = menu.findItem(R.id.action_search);
//	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
////        if(this instanceof MainActivity){
//        	 mSearchView.setOnQueryTextListener(this);
////        }
	   
//        search view	 
        	SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
             
            MenuItem searchItem = menu.findItem(R.id.action_search);
     	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            mSearchView.setSearchableInfo( 
             		searchManager.getSearchableInfo(getComponentName()));
	    
	    

	    return true;
	    
	    
	}
	/*
	 * add action for menu item is clicked (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		case R.id.action_add:
			
    		
			showPopup();
            return true;
        case R.id.action_search:
        	Intent intent = new Intent(context, MySearchableActivity.class);
    		context.startActivity(intent);
//			onSearchRequested();
//			startSearch("", false, null, false);
//            mSearchView.setIconified(false);
            return true;
//        case android.R.id.home:
//        	Toast.makeText(this, "Hello ", Toast.LENGTH_LONG).show();
//            break;
        case android.R.id.home:
        	if (slidingMenu.isMenuShowing()) {
				slidingMenu.showContent();
			} else {
				 slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
				slidingMenu.showMenu();
				slidingMenu.setBehindOffset(mScreenWidth / 8);// 设置菜单宽度
			}
		
		}
		return super.onOptionsItemSelected(item);
	}


    public void showPopup(){
        View menuItemView = findViewById(R.id.action_add);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.popup, popup.getMenu());
        popup.show();

    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.add_contact:
				Toast.makeText(context, "not implemented yet,please use send new mail",  Toast.LENGTH_SHORT).show();
				break;
			case R.id.new_group:
				Toast.makeText(context, "not implemented yet",  Toast.LENGTH_SHORT).show();
				break;
				
			case R.id.new_mail:
				Intent intent = new Intent(context, SendMailActivity.class);
	    		context.startActivity(intent);
				break;
		}
		
			
		// TODO Auto-generated method stub
		return false;
	}

}


