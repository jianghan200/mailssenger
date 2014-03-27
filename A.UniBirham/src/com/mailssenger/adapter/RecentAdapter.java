package com.mailssenger.adapter;

import com.mailssenger.R;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.activity.ChatActivity;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.model.MailModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.T;
import com.mailssenger.util.TimeUtil;



public class RecentAdapter extends BaseAdapter {
	
	private static String TAG = " >RecentAdapter";
	
	CommonApplication mApplication = CommonApplication.getInstance();
	SharedPreferencesUtil mSpUtil = mApplication.getSpUtil();
	Gson mGson = mApplication.getGson();
	UserDB mUserDB = mApplication.getUserDB();
	MessageDB mMsgDB = mApplication.getMessageDB();
	RecentDB mRecentDB = mApplication.getRecentDB();
	
	private LayoutInflater mInflater;
	
	private LinkedList<ConversationModel> mData;
	private Context context;
	
	public RecentAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		mMsgDB = CommonApplication.getInstance().getMessageDB();
		mRecentDB = CommonApplication.getInstance().getRecentDB();
		mUserDB = CommonApplication.getInstance().getUserDB();
	}
	
	public void setData(LinkedList<ConversationModel> data) {
		mData = data;
	}
	
	public void remove(int position) {
		if (position < mData.size()) {
			mData.remove(position);
			notifyDataSetChanged();
		}
	}

	public void remove(ConversationModel item) {
		if (mData.contains(item)) {
			mData.remove(item);
			notifyDataSetChanged();
		}
	}

	public void addFirst(ConversationModel item) {
		if (mData.contains(item)) {
			mData.remove(item);
		}
		mData.addFirst(item);
		L.i("addFirst: " + item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		RecentViewHolder viewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.recent_listview_item, null);
		
			viewHolder = new RecentViewHolder();
			viewHolder.recent_listview_row = (RelativeLayout) convertView
					.findViewById(R.id.recent_list_item_row);
			viewHolder.recent_listview_img = (ImageView) convertView
					.findViewById(R.id.recent_list_item_img);
			viewHolder.recent_listview_name = (TextView) convertView
					.findViewById(R.id.recent_list_item_name);
			viewHolder.recent_listview_time = (TextView) convertView
					.findViewById(R.id.recent_list_item_time);
			viewHolder.recent_listview_summary = (TextView) convertView
					.findViewById(R.id.recent_list_item_msg);
			viewHolder.recent_listview_tips = (TextView) convertView
					.findViewById(R.id.recent_list_item_tips);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (RecentViewHolder) convertView.getTag();
		}
		
		if (position % 2 == 0) {
			viewHolder.recent_listview_row.setBackgroundResource(R.color.grey);
		} else {
			viewHolder.recent_listview_row.setBackgroundResource(R.color.white);
		}
		
		//
		ConversationModel item = mData.get(position);
		
		String name = item.getName();
		if(name==null||name.equals("")){
			name = item.getEmail();
		}
		
		viewHolder.recent_listview_name.setText(name);//昵称
		
		if(item.getMessage().startsWith("{\"")){//whetehr start with json
			viewHolder.recent_listview_summary.setText(mGson.fromJson(item.getMessage(), MailModel.class).getSubject());
		}else{
			viewHolder.recent_listview_summary.setText(item.getMessage());
		}
//		if(item.getMessage().startsWith("{email:"))
//		
		
		viewHolder.recent_listview_time
		.setText(TimeUtil.getChatTime(item.getTime()));
		
		viewHolder.recent_listview_img
		.setImageResource(CommonApplication.heads[2]);//头像设置
		
		//获得当点会话的聊天对象 邮箱
		String hisEmail = item.getEmail();
		//新消息提示,与 他的对话新消息数,传入他的邮箱地址
		int num = mMsgDB.getNewCount(hisEmail);
		//新消息数目显示
		if (num > 0) {
			viewHolder.recent_listview_tips.setVisibility(View.VISIBLE);
			viewHolder.recent_listview_tips.setText(num + "");
		} else {
			viewHolder.recent_listview_tips.setVisibility(View.GONE);
		}
		
		//点击监听
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				ConversationModel item = mData.get(position);
				
				UserModel u = mUserDB.selectInfo(item.getEmail());

				T.dShowLong(context, mGson.toJson(u));
				//清空所有新消息
				mMsgDB.clearNewCount(u.getEmail());
				
				//将用户对象传给 ChatActivity.class
				Intent toChatIntent = new Intent(context,ChatActivity.class);
				toChatIntent.putExtra("user", u);
				context.startActivity(toChatIntent);
			}
		});

		return convertView;
	}

	final class RecentViewHolder {
		public RelativeLayout recent_listview_row;
		public ImageView recent_listview_img;
		public TextView recent_listview_name;
		public TextView recent_listview_time;
		public TextView recent_listview_summary;
		public TextView recent_listview_tips;
	}
}
