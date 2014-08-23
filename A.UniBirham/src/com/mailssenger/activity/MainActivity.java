package com.mailssenger.activity;


import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.mailssenger.CommonApplication;
import com.mailssenger.MainServiceCallback;
import com.mailssenger.R;
import com.mailssenger.Task;
import com.mailssenger.adapter.RecentAdapter;
import com.mailssenger.db.ConvDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.fragment.ConvFragment;
import com.mailssenger.mail.MailAccount;
import com.mailssenger.model.ConvModel;
import com.mailssenger.model.MsgModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.push.MyPushMessageReceiver;
import com.mailssenger.search.MySearchableActivity;
import com.mailssenger.service.MainService;
import com.mailssenger.util.L;
import com.mailssenger.util.NetUtil;
import com.mailssenger.util.T;

public class MainActivity extends BaseActivity implements OnClickListener,
MyPushMessageReceiver.EventHandler, MainServiceCallback,PopupMenu.OnMenuItemClickListener{
	
	//添加 MainServiceCallback 之后才可以添加任务
	private static String TAG = " >MainActivity";

	ConvFragment convFragment = null;
	
	private LinkedList<ConvModel> mRecentDatas;
	private RecentAdapter mRecentAdapter;
		
	//网络提醒
	private View mNetErrorView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		setContentView(R.layout.activity_main);
		
		setTitle("Mailssenger");
		
		mApplication.setHandler(handler);
		mRecentDatas = new LinkedList<ConvModel>();
		mRecentDatas = mConvDB.getRecentList();
		mRecentAdapter = new RecentAdapter(this);
		mRecentAdapter.setData(mRecentDatas);
	
		//start baidu push
		PushManager.startWork(getApplicationContext(),
		PushConstants.LOGIN_TYPE_API_KEY, CommonApplication.API_KEY);

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
				
				
				case MsgModel.MESSAGE_TYPE_NEW_USER:
					
					UserModel u = (UserModel) msg.obj;
					mUserDB.save(u);
					T.showShort(mApplication, "好友列表已更新!");
					
					//Demo 数据
					ConvModel recentItem = new ConvModel();
					recentItem.setEmail(u.getEmail());
					recentItem.setName(u.getNickName());
					recentItem.setMessage("Hi~ I am"+u.getNickName());
					recentItem.setNewNum(0);
					recentItem.setTime(System.currentTimeMillis());
					mConvDB.saveOrUpdate(recentItem);
					
					updateConvFragment();
					

					break;
					
				case MsgModel.MESSAGE_TYPE_TEXT:
					
					//接收到新的消息
					MsgModel msgItem = (MsgModel) msg.obj;
					msgItem.setNew(true);
					
					String hisEmail = msgItem.getSender();
					
					//只有存在好友关系,才可以发文本信息,所以收到文本消息的时候,用户必然存在
					UserModel user = mUserDB.getById(hisEmail);
					if(user!=null){
						
					}
					
					L.e(TAG,mGson.toJson(msgItem));
					L.e(TAG,"save msg");
					mMsgDB.save(msgItem);
					
					recentItem = new ConvModel(hisEmail, user.getAvatar(), user.getNickName(),
							msgItem.getMessage(), 0, System.currentTimeMillis());
					
					mConvDB.saveOrUpdate(recentItem);
					
					
					L.e(TAG,"I will be in updateConvFragment()");
					updateConvFragment();
//					mRecentAdapter.addFirst(recentItem);
					T.showShort(mApplication, user.getNickName() + ":" + msgItem.getMessage());
					break;
				default:
					break;
			}
			
		}
	};



	@Override
	public void onChatMessage(MsgModel chatMessage) {
		//将得到的消息交给handler处理
		Message handlerMsg = handler.obtainMessage(MsgModel.MESSAGE_TYPE_TEXT);
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
////				mConvDB.saveRecent(recentItem);
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
		 * 连续按两次返回键就退出
		 */
		private long firstTime;
	/**
	 * rewrite the sliding menu
	 */
	@Override
	public void onBackPressed() {
		
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
//		mRecentDatas = mConvDB.getRecentList();
//		mRecentAdapter.setData(mRecentDatas);
//		mRecentAdapter.notifyDataSetChanged();
	}
	
    public void initialUserDB(){
   	UserDB mUserDB =CommonApplication.getInstance().getUserDB();
   	ConvDB mConvDB=CommonApplication.getInstance().getConvDB();
		
		UserModel u  = new UserModel();
		u.setEmail("1129966399@qq.com");
		u.setNickName("Han Jiang");			
		mUserDB.save(u);
		
		u = new UserModel();
		u.setEmail("zxm024@bham.ac.uk");
		u.setNickName("Kenny Ma");			
		mUserDB.save(u);
		
		u = new UserModel();
		u.setEmail("HYW399@cs.bham.ac.uk");
		u.setNickName("Bowie");			
		mUserDB.save(u);
		
		u = new UserModel();
		u.setEmail("TYL375@cs.bham.ac.uk‎");
		u.setNickName("Issac");			
		mUserDB.save(u);
		
		u = new UserModel();
		u.setEmail("YXF373@cs.bham.ac.uk");
		u.setNickName("Seffy");			
		mUserDB.save(u);
		
		//Demo 数据
		ConvModel recentItem = new ConvModel();
		recentItem.setEmail("zxm024@bham.ac.uk");
		recentItem.setName("Kenny Ma");
		recentItem.setMessage("Here is Kenny");
		recentItem.setNewNum(0);
		recentItem.setTime(System.currentTimeMillis());
		mConvDB.saveOrUpdate(recentItem);

		recentItem = new ConvModel();
		recentItem.setEmail("1129966399@qq.com");
		recentItem.setName("Han Jiang");
		recentItem.setMessage("It is a Chinese name");
		recentItem.setNewNum(0);
		recentItem.setTime(System.currentTimeMillis());
		mConvDB.saveOrUpdate(recentItem);

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
    	mRecentDatas = mConvDB.getRecentList();
    	
    	
		mRecentAdapter.setData(mRecentDatas);
		mRecentAdapter.notifyDataSetChanged();
    }

    private SearchView mSearchView;
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	 
//	    MenuItem searchItem = menu.findItem(R.id.action_search);
//	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        mSearchView.setOnQueryTextListener(this);

	  	    
//        	SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//             
//          MenuItem searchItem = menu.findItem(R.id.action_search);
//     	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//          mSearchView.setSearchableInfo( 
//             		searchManager.getSearchableInfo(getComponentName()));
	    
	    return true;
	      
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		//加号按键监听
		case R.id.action_add:
			showPopup();
            return true;
        //搜索按钮监听
        case R.id.action_search:
        	Intent intent = new Intent(context, MySearchableActivity.class);
    		context.startActivity(intent);
//			onSearchRequested();
//			startSearch("", false, null, false);
//          mSearchView.setIconified(false);
            return true;
        //主按键监听
        case android.R.id.home:
        	Toast.makeText(this, "Hello ", Toast.LENGTH_LONG).show();
        	break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Show Popup Menu
	 */
    public void showPopup(){
        View menuItemView = findViewById(R.id.action_add);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.popup, popup.getMenu());
        popup.show();
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		/*
		 * 子菜单项的点击监听
		 */
		
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
		
		return false;
	}

}


