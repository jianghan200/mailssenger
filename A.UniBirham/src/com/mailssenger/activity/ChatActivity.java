package com.mailssenger.activity;

import com.mailssenger.R;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.adapter.MessageAdapter;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.mail.MailSender;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.model.MessageModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.push.MyPushMessageReceiver;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.SendMsgAsyncTask;
import com.mailssenger.util.T;
import com.mailssenger.util.UIHelper;
import com.mailssenger.xlistview.MsgListView;
import com.mailssenger.xlistview.MsgListView.IXListViewListener;


public class ChatActivity extends BaseActivity implements
		MyPushMessageReceiver.EventHandler, OnTouchListener, OnClickListener,
		IXListViewListener {
	
	private static String TAG = " >ChatActivity";
	public static final int NEW_MESSAGE = 0x001;// 收到消息

	private static int MsgPagerNum;


	//
	private Button sendBtn;
	private ImageButton mailBtn;
	private boolean isFaceShow;
	
	private EditText msgEt;

	//输入法管理器
	private WindowManager.LayoutParams params;
	private InputMethodManager imm;
	
	private List<String> keys;
	private MessageAdapter msgAdapter;
	private MsgListView mMsgListView;
	private UserModel hisUserModel;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == NEW_MESSAGE) {
				
			}

			// String message = (String) msg.obj;
			MessageModel chatMessage = (MessageModel) msg.obj;
			//如果能跟用户聊天,那么数据库中一定有该用户
			String hisEmail = chatMessage.getEmail();
			//只处理自己的信息
			if (!hisEmail.equals(hisUserModel.getEmail()))// 如果不是当前正在聊天对象的消息，不处理
				return;
			
			chatMessage.setCome(true);	
			
			if(chatMessage.getMsgType() == MessageModel.MESSAGE_TYPE_TEXT){

				msgAdapter.upDateMsg(chatMessage);
				
				//信息  发给谁,谁发来
				// 发出去的时候,email 存接收人邮箱地址,isCome =false
				// 接收信息的时候,email 为发送人地址, isCome = true
				//以上为错误思想,无法解决   当他接收我发送消息,他无法知道是谁发给他的
				L.e(TAG,mGson.toJson(chatMessage));
				L.e(TAG,"save msg");
				mMsgDB.saveMsg(hisEmail, chatMessage);

				ConversationModel recentItem = new ConversationModel();
				recentItem.setEmail(hisEmail);
				recentItem.setName(hisUserModel.getNickName());
				recentItem.setMessage(chatMessage.getMessage());
				recentItem.setNewNum(0);
				recentItem.setTime(System.currentTimeMillis());

				mRecentDB.saveRecent(recentItem);
			}
			
			if(chatMessage.getMsgType() < MessageModel.MESSAGE_TYPE_MAIL){
				
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.chat_main);

		hisUserModel = (UserModel) getIntent().getSerializableExtra("user");
		if (hisUserModel == null) {// 如果为空，直接关闭
			finish();
		}
		
		setTitle(hisUserModel.getNickName());

		MsgPagerNum = 0;
		msgAdapter = new MessageAdapter(this, initMsgData(),hisUserModel);
		
		initView();
		
		L.e(mSpUtil.getEmail());
		
		mUserDB =CommonApplication.getInstance().getUserDB();

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
//		mHomeWatcher = new HomeWatcher(this);
//		mHomeWatcher.setOnHomePressedListener(this);
//		mHomeWatcher.startWatch();
		
		MyPushMessageReceiver.ehList.add(this);// 监听推送的消息
		L.e(TAG,"I am resume");
		System.out.println(MyPushMessageReceiver.ehList);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		imm.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);

		super.onPause();
//		mHomeWatcher.setOnHomePressedListener(null);
//		mHomeWatcher.stopWatch();
		MyPushMessageReceiver.ehList.remove(this);// 移除监听
		L.e(TAG,"I am paused");
		System.out.println(MyPushMessageReceiver.ehList);
	}

	private void initData() {

	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<MessageModel> initMsgData() {
		List<MessageModel> list = mMsgDB.getMsg(hisUserModel.getEmail(),
				MsgPagerNum);
		List<MessageModel> msgList = new ArrayList<MessageModel>();// 消息对象数组
		if (list.size() > 0) {
			for (MessageModel entity : list) {
				if (entity.getEmail().equals("")) {
					entity.setEmail(hisUserModel.getEmail());
				}
//				if (entity.getHeadImg() < 0) {
//					entity.setHeadImg(hisUserModel.getHeadIcon());
//				}
				msgList.add(entity);
			}
		}
		return msgList;

	}


	private void initView() {
		// TODO Auto-generated method stub
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		params = getWindow().getAttributes();

		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
		// 触摸ListView隐藏表情和输入法
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		mMsgListView.setAdapter(msgAdapter);
		mMsgListView.setSelection(msgAdapter.getCount() - 1);
		
		
		sendBtn = (Button) findViewById(R.id.send_btn);
		mailBtn = (ImageButton) findViewById(R.id.face_btn);
		msgEt = (EditText) findViewById(R.id.msg_et);
		
		
		//Action bar
//		msgEt.setOnTouchListener(this);
//		mTitle = (TextView) findViewById(R.id.ivTitleName);
//		mTitle.setText(hisUserModel.getNick());
//		mTitleLeftBtn = (TextView) findViewById(R.id.ivTitleBtnLeft);
//		mTitleLeftBtn.setVisibility(View.VISIBLE);
//		// mTitleRightBtn = (TextView) findViewById(R.id.ivTitleBtnRigh);
//		mTitleLeftBtn.setOnClickListener(this);
		
		// mTitleRightBtn.setOnClickListener(this);
		msgEt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
//						faceLinearLayout.setVisibility(View.GONE);
//						isFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
		msgEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					sendBtn.setEnabled(true);
				} else {
					sendBtn.setEnabled(false);
				}
			}
		});
		mailBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
	}

	@Override
	public void onBind(String method, int errorCode, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(String title, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if (!isNetConnected)
			T.showShort(this, "网络连接已断开");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.face_btn:
			if (!isFaceShow) {
//				imm.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
//				try {
//					Thread.sleep(80);// 解决此时会黑一下屏幕的问题
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				faceLinearLayout.setVisibility(View.VISIBLE);
				isFaceShow = true;
				
				mailBtn.setImageResource(R.drawable.unibirham_email);
			} else {
//				faceLinearLayout.setVisibility(View.GONE);
				isFaceShow = false;
				mailBtn.setImageResource(R.drawable.pop_btn_face_selector);
			}
			break;
		case R.id.send_btn:// 发送消息
			String msg = msgEt.getText().toString();
			//如果用户的user id 不为空,否则默认邮件发送
			if(!(hisUserModel.getUserId() == null)){
				if (isFaceShow) {
					new Thread(sendMailRunnable).start();
					break;
					
				}

				//发送信息,
				//我发给对方,当然要写上自己的地址啊!
				//这样对方收到才知道是我发给她的啊
				//但是在只有发送方的地址的情况下,必须分开表
				MessageModel msgItem = new MessageModel( mSpUtil.getEmail(),
						MessageModel.MESSAGE_TYPE_TEXT, msg ,System.currentTimeMillis());
			
				msgAdapter.upDateMsg(msgItem);
				// if (msgAdapter.getCount() - 10 > 10) {
				// L.i("begin to remove...");
				// msgAdapter.removeHeadMsg();
				// MsgPagerNum--;
				// }
				
				mMsgListView.setSelection(msgAdapter.getCount() - 1);
				
				//存信息,为自己发的信息,email 设为对方的地址,isCome 为false
				mMsgDB.saveMsg(hisUserModel.getEmail(), msgItem);
				msgEt.setText("");
				
				L.e(mGson.toJson(msgItem));
				//发送信息
				new SendMsgAsyncTask(mGson.toJson(msgItem), hisUserModel.getUserId())
						.send();
				
				ConversationModel recentItem = new ConversationModel(hisUserModel.getEmail(), hisUserModel.getAvatar(), hisUserModel.getNickName(),
						msg, 0, System.currentTimeMillis());
				mRecentDB.saveRecent(recentItem);
			}else{
//				直接邮件发送
				new Thread(sendMailRunnable).start();
			}
			
			break;
//		case R.id.ivTitleBtnLeft:
//			finish();
//			break;
//		case R.id.ivTitleBtnRigh:
//			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.msg_listView:
//			imm.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
//			faceLinearLayout.setVisibility(View.GONE);
//			isFaceShow = false;
			break;
		case R.id.msg_et:
//			imm.showSoftInput(msgEt, 0);
//			faceLinearLayout.setVisibility(View.GONE);
//			isFaceShow = false;
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onNewFriend(UserModel u) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		MsgPagerNum++;
		List<MessageModel> msgList = initMsgData();
		int position = msgAdapter.getCount();
		msgAdapter.setMessageList(msgList);
		mMsgListView.stopRefresh();
		mMsgListView.setSelection(msgAdapter.getCount() - position - 1);
		L.i("MsgPagerNum = " + MsgPagerNum + ", msgAdapter.getCount() = "
				+ msgAdapter.getCount());
	}

	//下拉载入更多
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	//收到聊天信息
	@Override
	public void onChatMessage(MessageModel chatMessage) {
		//obtainMessage()
		//Value to assign to the returned Message.what field.
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = chatMessage;
		handler.sendMessage(handlerMsg);
	}

	
	
	Runnable sendMailRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			//try to send email
			MailSender sender = new MailSender(CommonApplication.SACCOUNT,CommonApplication.PASSWORD);
			
			Log.e("Birham : sendMail", "I am in run");
			
			String header = "Dear " +hisUserModel.getNickName() +"\n";
			String content = msgEt.getText().toString();
			String footer = "\n\nBest wishes\n" + mSpUtil.getNick() +"\n";
			String infoString ="\n\n=======\n This mail is send by Mailssenger";
		
			
			String mailContent = header +content +footer+infoString;
			try {
				
				String senderEmailAddr = "\""+mSpUtil.getNick()+"\"<"+mSpUtil.getEmail()+">";
				String recieverEmailAddr = "\""+hisUserModel.getEmail()+"\"<"+hisUserModel.getEmail()+">";
				sender.sendMail(" no topic ", mailContent, senderEmailAddr, recieverEmailAddr, "",
						"");
				T.dShowLong(context, "Mail has been send successfully!");
				
				
//				MailModel mailModel = new MailModel();
//				mailModel.setSubject("no topic");
//				mailModel.setContent( mailContent);
//				ChatMessageItem chatMessage = new ChatMessageItem();
//				chatMessage.setEmail(mSpUtil.getEmail());
//				chatMessage.setMsgType(ChatMessageItem.MESSAGE_TYPE_MAIL);
//				chatMessage.setCome(false);
//				chatMessage.setNew(false);
//				chatMessage.setTime(System.currentTimeMillis());
//				chatMessage.setMessage(message);
//				
//				msgAdapter.upDateMsg(chatMessage);
//				
//				//信息  发给谁,谁发来
//				// 发出去的时候,email 存接收人邮箱地址,isCome =false
//				// 接收信息的时候,email 为发送人地址, isCome = true
//				//以上为错误思想,无法解决   当他接收我发送消息,他无法知道是谁发给他的
//				L.e(TAG,mGson.toJson(chatMessage));
//				L.e(TAG,"save msg");
//				mMsgDB.saveMsg(hisEmail, chatMessage);
//
//				RecentConversationModel recentItem = new RecentConversationModel();
//				recentItem.setEmail(hisEmail);
//				recentItem.setName(hisUserModel.getNickName());
//				recentItem.setMessage(chatMessage.getMessage());
//				recentItem.setNewNum(0);
//				recentItem.setTime(System.currentTimeMillis());
//
//				mRecentDB.saveRecent(recentItem);
				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
//			Toast.makeText(context, "Mail sending thread is running!",
//					Toast.LENGTH_LONG).show();
		}
	};
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
        case android.R.id.home:
        	this.finish();
            break;
		}
		return super.onOptionsItemSelected(item);
		
	}


}