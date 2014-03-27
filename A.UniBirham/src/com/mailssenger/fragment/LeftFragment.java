package com.mailssenger.fragment;

import com.baidu.android.pushservice.PushManager;
import com.mailssenger.CommonApplication;
import com.mailssenger.adapter.MenuAdapter;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.UserModel;
import com.mailssenger.slidinglayer.SlidingLayer;
import com.mailssenger.slidinglayer.SlidingLayer.OnInteractListener;
import com.mailssenger.switchbtn.SwitchButton;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.T;

import net.tsz.afinal.annotation.sqlite.OneToMany;
import com.mailssenger.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LeftFragment extends Fragment implements OnClickListener,
OnCheckedChangeListener, OnInteractListener {
	
	private CommonApplication mApplication;
	private SharedPreferencesUtil mSpUtil;
	private UserDB mUserDB;
	private LayoutInflater mInflater;
	
	private SwitchButton mMsgNotifySwitch;
	private SwitchButton mMsgSoundSwitch;
	private SwitchButton mShowHeadSwitch;
		
	private View mAcountInfo;
	private View mFeedBack;
	private SlidingLayer mSlidingLayer;
	private Button mExitAppBtn, mComfirmExitBtn;

	private View mExitConfirmView;
	private View mAcountSetView;
	private View mMyInfoView;
	private View mFeedBackView;
	private View mAboutView;

	private EditText mFeedBackET;
	private Button mFeedBackBtn;

	private Button mAccountBtn;
	
	
	//個人頭像
	private ImageView mHead;
	private TextView mNick;
	private View mAccountSetting;
	private EditText mNickEt;

	//原始的ListView
//	private ListView menuListView;
//	private MenuAdapter adapter;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	public SlidingLayer getSlidingLayer() {
		return mSlidingLayer;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(getActivity());
		mApplication = CommonApplication.getInstance();
		mUserDB = mApplication.getUserDB();
		mSpUtil = mApplication.getSpUtil();

		initAcountSetView();
		initMyInfoView();
		initExitView();
		initFeedBackView();
		initAboutView();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		L.i("right onCreateView");
		//get view
		View view = inflater.inflate(R.layout.slidingmenu_left_frame, container,
				false);
		initView(view);

		return view;
	}

	
	private void initMyInfoView() {
		// TODO Auto-generated method stub
		mMyInfoView = mInflater.inflate(R.layout.my_info, null);
	}

	private void initFeedBackView() {
		// TODO Auto-generated method stub
		mFeedBackView = mInflater.inflate(R.layout.feed_back_view, null);
		mFeedBackET = (EditText) mFeedBackView.findViewById(R.id.fee_back_edit);
		mFeedBackBtn = (Button) mFeedBackView.findViewById(R.id.feed_back_btn);
		mFeedBackBtn.setOnClickListener(this);
	}

	
	private void initAcountSetView() {
		// TODO Auto-generated method stub
		mAcountSetView = mInflater.inflate(R.layout.accout_set_view, null);
		mAccountBtn = (Button) mAcountSetView.findViewById(R.id.acount_set_btn);
		mAccountBtn.setOnClickListener(this);
		
	}

	private void initExitView() {
		// TODO Auto-generated method stub
		mExitConfirmView = mInflater.inflate(R.layout.exit_app_confirm, null);
		mComfirmExitBtn = (Button) mExitConfirmView
				.findViewById(R.id.confirm_exit_btn);
		mComfirmExitBtn.setOnClickListener(this);
	}

	// 关于
	private void initAboutView() {
		// TODO Auto-generated method stub
		mAboutView = mInflater.inflate(R.layout.about, null);
		TextView app_information = (TextView) mAboutView
				.findViewById(R.id.app_information);
		String myBlog = String.format("<a href=\"%s\">%s</a>",
				getString(R.string.my_blog_url),
				getString(R.string.my_blog_url));
		app_information.setText(Html.fromHtml(getString(
				R.string.app_information, myBlog)));
		app_information.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		//load the menu list
//		menuListView = (ListView) this.getActivity().findViewById(R.id.menu_list);
//		
//		menuListView.setDivider(null);
//		adapter = new MenuAdapter(this.getActivity());
//		menuListView.setAdapter(adapter);

	}
	
	private void initView(View view) {
		// title
//		view.findViewById(R.id.ivTitleBtnLeft).setVisibility(View.GONE);
//		view.findViewById(R.id.ivTitleBtnRigh).setVisibility(View.GONE);

		mSlidingLayer = (SlidingLayer) view
				.findViewById(R.id.left_sliding_layer);
		mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_LEFT);
		mExitAppBtn = (Button) view.findViewById(R.id.left_menu_exit);

		mMsgNotifySwitch = (SwitchButton) view
				.findViewById(R.id.message_notify_switch);
		mMsgSoundSwitch = (SwitchButton) view
				.findViewById(R.id.message_sound_switch);

		mShowHeadSwitch = (SwitchButton) view
				.findViewById(R.id.show_head_switch);
		mMsgNotifySwitch.setChecked(mSpUtil.getMsgNotify());
		mMsgSoundSwitch.setChecked(mSpUtil.getMsgSound());

		mShowHeadSwitch.setChecked(mSpUtil.getShowHead());


//		mMyProfile = view.findViewById(R.id.my_profile);
		mAcountInfo = view.findViewById(R.id.set_about);
		mFeedBack = view.findViewById(R.id.set_feedback);
		
		
		mHead = (ImageView) view.findViewById(R.id.userinfo_avatar);

		mNick = (TextView) view.findViewById(R.id.userinfo_name);
		mNick.setText(mSpUtil.getNick());

		mAccountSetting = view.findViewById(R.id.userinfo_setting);
		
		
		setListener();
		

	}

	private void setListener() {
		// TODO Auto-generated method stub

		mSlidingLayer.setOnInteractListener(this);
		mMsgNotifySwitch.setOnCheckedChangeListener(this);
		mMsgSoundSwitch.setOnCheckedChangeListener(this);
		mShowHeadSwitch.setOnCheckedChangeListener(this);
		
//		mMyProfile.setOnClickListener(this);
//		mFaceJazzEffect.setOnClickListener(this);
		mAcountInfo.setOnClickListener(this);
		mFeedBack.setOnClickListener(this);
		mExitAppBtn.setOnClickListener(this);

		
		mAccountSetting.setOnClickListener(this);
//		SlidingMenu slidingMenu = ((UniBirhamActivity) getActivity())
//				.getSlidingMenu();
//		// 监听菜单关闭，来关闭弹出的设置view
//		if (slidingMenu != null)
//			slidingMenu.setOnCloseListener(new OnCloseListener() {
//
//				@Override
//				public void onClose() {
//					// TODO Auto-generated method stub
//					if (mSlidingLayer != null && mSlidingLayer.isOpened()) {
//						mSlidingLayer.closeLayer(true);
//					}
//				}
//			});
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.left_menu_exit:
			mSlidingLayer.removeAllViews();// 先移除所有的view,不然会报错
			if (!mSlidingLayer.isOpened()) {
				mSlidingLayer.addView(mExitConfirmView);
				mSlidingLayer.openLayer(true);
			}
			break;
		case R.id.confirm_exit_btn:
			if (mSlidingLayer.isOpened()) {
				mSlidingLayer.closeLayer(true);
			}
			if (PushManager.isPushEnabled(getActivity())) {
				PushManager.stopWork(getActivity());
			}
			getActivity().finish();
			break;
		case R.id.my_profile:
			mSlidingLayer.removeAllViews();
			if (!mSlidingLayer.isOpened()) {
				mSlidingLayer.addView(mMyInfoView);
				mSlidingLayer.openLayer(true);
			}
			break;
		case R.id.set_about:
			mSlidingLayer.removeAllViews();
			if (!mSlidingLayer.isOpened()) {
				mSlidingLayer.addView(mAboutView);
				mSlidingLayer.openLayer(true);
			}
			break;
		case R.id.set_feedback:
			mSlidingLayer.removeAllViews();
			if (!mSlidingLayer.isOpened()) {
				mSlidingLayer.addView(mFeedBackView);
				mSlidingLayer.openLayer(true);
			}
			break;
		case R.id.feed_back_btn:
			String content = mFeedBackET.getText().toString();
			if (!TextUtils.isEmpty(content)) {
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "推聊Android客户端 - 信息反馈");
				intent.putExtra(Intent.EXTRA_TEXT, content);
				intent.setData(Uri.parse("mailto:way.ping.li@gmail.com"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getActivity().startActivity(intent);
				if (mSlidingLayer.isOpened()) {
					mSlidingLayer.closeLayer(true);
				}
			} else {
				T.showShort(getActivity(), "请输入一点点内容嘛！");
			}
			break;
		case R.id.accountSetting:
			mSlidingLayer.removeAllViews();
			if (!mSlidingLayer.isOpened()) {
				mSlidingLayer.addView(mAcountSetView);
				mSlidingLayer.openLayer(true);
			}
			break;
	
		case R.id.acount_set_btn:
			String nick = mNickEt.getText().toString();
			if (TextUtils.isEmpty(nick)) {
				T.showShort(getActivity(), R.string.first_start_tips);
				return;
			}
			mSpUtil.setNick(nick);
			UserModel u = new UserModel();
			u.setNickName(nick);
//			mSpUtil.getUserId(), mSpUtil.getChannelId(),
//			nick, mHeadWheel.getCurrentItem(), 0
			mUserDB.addUser(u);
			
			//用户个人信息更新
//			ChatMessageItem msgItem = new ChatMessageItem();
//			System.currentTimeMillis(), "hi",
//			mSpUtil.getTag()
//			new SendMsgAsyncTask(mApplication.getGson().toJson(msgItem), "")
//					.send();// 发送信息更新一下
			if (mSlidingLayer.isOpened()) {
				mSlidingLayer.closeLayer(true);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.message_notify_switch:
			mSpUtil.setMsgNotify(isChecked);
			break;
		case R.id.message_sound_switch:
			mSpUtil.setMsgSound(isChecked);
			break;
		case R.id.pullrefresh_sound_switch:
			mSpUtil.setPullRefreshSound(isChecked);
			break;
		case R.id.show_head_switch:
			mSpUtil.setShowHead(isChecked);
			break;
		default:
			break;
		}
	}

	@Override
	public void onOpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		// mSlidingLayer.removeAllViews();
	}

	@Override
	public void onOpened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClosed() {
		// TODO Auto-generated method stub
		mSlidingLayer.removeAllViews();
	}

}
