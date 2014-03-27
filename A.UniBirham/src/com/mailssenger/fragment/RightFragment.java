package com.mailssenger.fragment;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.mailssenger.CommonApplication;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.UserModel;
import com.mailssenger.slidinglayer.SlidingLayer;
import com.mailssenger.slidinglayer.SlidingLayer.OnInteractListener;
import com.mailssenger.switchbtn.SwitchButton;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.T;



public class RightFragment extends Fragment implements OnClickListener, OnInteractListener {


	private CommonApplication mApplication;
	private SharedPreferencesUtil mSpUtil;
	private UserDB mUserDB;
	private LayoutInflater mInflater;

	private SlidingLayer mSlidingLayer;

	private View mAcountSetView;

	private Button mAccountBtn;
	private EditText mNickEt;

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

	}

	private void initAcountSetView() {
		// TODO Auto-generated method stub
		mAcountSetView = mInflater.inflate(R.layout.accout_set_view, null);
		mAccountBtn = (Button) mAcountSetView.findViewById(R.id.acount_set_btn);
		mAccountBtn.setOnClickListener(this);
		mNickEt = (EditText) mAcountSetView.findViewById(R.id.nick_ed);

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.i("right onCreateView");
		View view = inflater.inflate(R.layout.slidingmenu_right_frame, container,
				false);
		initView(view);
		return view;
	}

	private void initView(View view) {
	
		mSlidingLayer = (SlidingLayer) view
				.findViewById(R.id.right_sliding_layer);
		
	
		setListener();

	}

	private void setListener() {
		// TODO Auto-generated method stub
		mSlidingLayer.setOnInteractListener(this);

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
