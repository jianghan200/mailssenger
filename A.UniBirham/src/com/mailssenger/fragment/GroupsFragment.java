package com.mailssenger.fragment;

import com.mailssenger.R;

import java.util.LinkedList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.adapter.RecentAdapter;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.util.SharedPreferencesUtil;

public class GroupsFragment extends Fragment{
	
	
	//初始化工具
	private CommonApplication mApplication;
	private SharedPreferencesUtil mSpUtil;
	private UserDB mUserDB;
	private MessageDB mMsgDB;
	private MediaPlayer mMediaPlayer;
	private Gson mGson;
	
	//最近聊天相关
	private RecentDB mRecentDB;
	private ListView mRecentListView;
	private TextView mEmpty;
	private static RecentAdapter mRecentAdapter;
	private LinkedList<ConversationModel> mRecentDatas;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mApplication = CommonApplication.getInstance();
		mSpUtil = mApplication.getSpUtil();
		mGson = mApplication.getGson();
		mUserDB = mApplication.getUserDB();
		mMsgDB = mApplication.getMessageDB();
		mRecentDB = mApplication.getRecentDB();
		

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//get view
		View view = inflater.inflate(R.layout.fragment_groups, null);
		
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		


	}
	

}
