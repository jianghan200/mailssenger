package com.mailssenger.fragment;

import com.mailssenger.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.activity.ChatActivity;
import com.mailssenger.adapter.RecentAdapter;
import com.mailssenger.contacts.CharacterParser;
import com.mailssenger.contacts.ClearEditText;
import com.mailssenger.contacts.PinyinComparator;
import com.mailssenger.contacts.SideBar;
import com.mailssenger.contacts.SortAdapter;
import com.mailssenger.contacts.UserSortModel;
import com.mailssenger.contacts.SideBar.OnTouchingLetterChangedListener;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;

public class ContactsFragment extends Fragment{
	
	private Activity context;
	
	//初始化工具
	CommonApplication mApplication;
	private SharedPreferencesUtil mSpUtil;
	private UserDB mUserDB;
	private MessageDB mMsgDB;
	private MediaPlayer mMediaPlayer;
	private Gson mGson;
	
	//联系人相关
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	//联系人相关工具
	private CharacterParser characterParser;
	private List<UserSortModel> SourceDateList;
	//根据拼音来排列ListView里面的数据类
	private PinyinComparator pinyinComparator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mApplication = CommonApplication.getInstance();
		mSpUtil = mApplication.getSpUtil();
		mGson = mApplication.getGson();
		mUserDB = mApplication.getUserDB();
		mMsgDB = mApplication.getMessageDB();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//get view
		View view = inflater.inflate(R.layout.fragment_contacts, null);
		
		sideBar = (SideBar) view.findViewById(R.id.contacts_sidebar);
		dialog = (TextView) view.findViewById(R.id.contacts_dialog);
		sortListView = (ListView) view.findViewById(R.id.contacts_listview);
		mClearEditText = (ClearEditText) view.findViewById(R.id.contacts_filter_edit);
		
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		context= this.getActivity();
		
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		
		SourceDateList = filledData();
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this.getActivity(), SourceDateList);
		sortListView.setAdapter(adapter);

		
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String email = ((UserSortModel)adapter.getItem(position)).getEmail();
				UserModel u = (UserModel) mUserDB.selectInfo(email);
				L.d(u.toString());
				mMsgDB.clearNewCount(u.getEmail());// 新消息置空
				
				Intent toChatIntent = new Intent(context,ChatActivity.class);
				toChatIntent.putExtra("user", u);
				startActivity(toChatIntent);
				
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Toast.makeText(context, ((UserSortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
				Toast.makeText(context, ((UserSortModel)adapter.getItem(position)).getEmail(), Toast.LENGTH_SHORT).show();
			}
		});
		
		

		sideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				
			}
		});

	
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}
	



	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<UserSortModel> filledData(){
		
		//get all the user
		List<UserModel> mUserList= mUserDB.getUser();
	 
		
		List<UserSortModel> mSortList = new ArrayList<UserSortModel>();
		if (mUserList.size()>0){
			for(int i=0; i<mUserList.size(); i++){

				UserSortModel sortModel = new UserSortModel();
				sortModel.setName(mUserList.get(i).getNickName());
				sortModel.setEmail((mUserList.get(i).getEmail()));
				
				
				if (sortModel.getName()==null){
					sortModel.setSortLetters("#");
				}else{
					//汉字转换成拼音
					String pinyin = characterParser.getSelling(sortModel.getName());
					String sortString = pinyin.substring(0, 1).toUpperCase();
					
					// 正则表达式，判断首字母是否是英文字母
					if(sortString.matches("[A-Z]")){
						sortModel.setSortLetters(sortString.toUpperCase());
					}else{
						sortModel.setSortLetters("#");
					}
				}
				
				
				mSortList.add(sortModel);
			}
		}
		
		return mSortList;
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<UserSortModel> filterDateList = new ArrayList<UserSortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(UserSortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	

}
