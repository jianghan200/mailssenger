package com.mailssenger.fragment;

import com.mailssenger.CommonApplication;
import com.mailssenger.activity.MainActivity;
import com.mailssenger.adapter.RecentAdapter;
import com.mailssenger.db.RecentDB;

import com.mailssenger.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ConvFragment extends Fragment{
	
	
	//初始化工具
	private CommonApplication mApplication;
	
	//最近聊天相关
	private RecentDB mRecentDB;
	private ListView mRecentListView;
	private TextView mEmpty;
	private RecentAdapter mRecentAdapter;

	
	public ConvFragment(RecentAdapter adapter){
		mRecentAdapter= adapter;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mApplication = CommonApplication.getInstance();
		mRecentDB = mApplication.getRecentDB();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//get view
		View view = inflater.inflate(R.layout.fragment_conv, null);
		

		//两个ListView
		mRecentListView =(ListView) view.findViewById(R.id.conv_listview);
		mEmpty = (TextView) view.findViewById(R.id.conv_empty);
		//初始化为空
		//initial recent data
		mRecentListView.setEmptyView(mEmpty);
		
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
//		mRecentAdapter= (MainActivity)getActivity();
		mRecentListView.setAdapter(mRecentAdapter);
		
	}




}
