package com.mailssenger.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.R;
import com.mailssenger.activity.ChatActivity;
import com.mailssenger.contacts.CharacterParser;
import com.mailssenger.contacts.ClearEditText;
import com.mailssenger.contacts.PinyinComparator;
import com.mailssenger.contacts.SideBar;
import com.mailssenger.contacts.SideBar.OnTouchingLetterChangedListener;
import com.mailssenger.contacts.SortAdapter;
import com.mailssenger.contacts.UserSortModel;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.UserModel;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;

public class MySearchableActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{
	
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
	    	this.context = this;
	    	super.onCreate(savedInstanceState);
	    	mApplication = CommonApplication.getInstance();
			mSpUtil = mApplication.getSpUtil();
			mGson = mApplication.getGson();
			mUserDB = mApplication.getUserDB();
			mMsgDB = mApplication.getMessageDB();
			
			
			setContentView(R.layout.fragment_contacts);
			sideBar = (SideBar) findViewById(R.id.contacts_sidebar);
			dialog = (TextView) findViewById(R.id.contacts_dialog);
			sortListView = (ListView) findViewById(R.id.contacts_listview);
			
			blank_search();
		
	       
	        handleIntent(getIntent());
	    }

	    @Override
	    protected void onNewIntent(Intent intent) {
	        
	        handleIntent(intent);
	    }
	    
	    private void blank_search(){
	    	//实例化汉字转拼音类
			characterParser = CharacterParser.getInstance();
			pinyinComparator = new PinyinComparator();
			
			SourceDateList = filledData();
			// 根据a-z进行排序源数据
			Collections.sort(SourceDateList, pinyinComparator);
			adapter = new SortAdapter(this, SourceDateList);
			sortListView.setAdapter(adapter);

			
			sortListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					L.d("hello");
					String email = ((UserSortModel)adapter.getItem(position)).getEmail();
					L.d("emial is "+email);
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
	    }

	    private void handleIntent(Intent intent) {
	    	String query = intent.getStringExtra(SearchManager.QUERY) ;
	    	SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,  
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);  
    	    suggestions.saveRecentQuery(query, null);  
	    	

	        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	            if(query!=null && !query.equals("")){
	            	filterData(query);  
	            }
	        	
	            Log.e("hello","search!!!");
	            //use the query to search your data somehow
	        }
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
						if(sortString.matches("[A-Za-z]")){
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



		@Override
		public boolean onQueryTextChange(String arg0) {
			// TODO Auto-generated method stub
			filterData(arg0);
			return false;
		}



		@Override
		public boolean onQueryTextSubmit(String arg0) {
			filterData(arg0);  
			return false;
		}
		
		
		  private SearchView mSearchView;
			 
			@Override
			public boolean onCreateOptionsMenu(Menu menu) {
			    getMenuInflater().inflate(R.menu.main, menu);
			 
			    MenuItem searchItem = menu.findItem(R.id.action_search);
			    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			    mSearchView.setOnQueryTextListener(this);

			   
		        	 
//		        	 SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//		             
//		             MenuItem searchItem = menu.findItem(R.id.action_search);
//		     	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//		             mSearchView.setSearchableInfo( 
//		             		searchManager.getSearchableInfo(getComponentName()));
		        
//		        MenuItem addItem = menu.findItem(R.id.action_add);
//			    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//			    return true;
		        
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
			

		        case android.R.id.home:
		        	context.finish();
				
				}
				return super.onOptionsItemSelected(item);
			}
				
}
