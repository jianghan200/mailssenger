package com.mailssenger.adapter;

import com.mailssenger.R;

import java.util.List;
import java.util.Map;

import com.mailssenger.activity.MailContentActivity;
import com.mailssenger.util.TimeUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainListViewAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;

	private List<Map<String, Object>> mData;

	public void setData(List<Map<String, Object>> list) {
		mData = list;
	}

	public MainListViewAdapter(Context context) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		MenuViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.main_listview_item, null);

			viewHolder = new MenuViewHolder();
			viewHolder.main_listview_row = (LinearLayout) convertView
					.findViewById(R.id.main_listview_row);
			viewHolder.main_listview_feature_img = (ImageView) convertView
					.findViewById(R.id.main_listview_feature_img);
			viewHolder.main_listview_name = (TextView) convertView
					.findViewById(R.id.main_listview_name);
			viewHolder.main_listview_time = (TextView) convertView
					.findViewById(R.id.main_listview_time);
			viewHolder.main_listview_summary = (TextView) convertView
					.findViewById(R.id.main_listview_summary);
			viewHolder.main_listview_tips = (ImageView) convertView
					.findViewById(R.id.main_listview_tips);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MenuViewHolder) convertView.getTag();
		}
		
		if (position % 2 == 0) {
			viewHolder.main_listview_row.setBackgroundResource(R.color.grey);
		} else {
			viewHolder.main_listview_row.setBackgroundResource(R.color.white);
		}
		String read =(String) mData.get(position).get("flags");
		
		
//		if(read == "read" ){
//			viewHolder.main_listview_feature_img.setImageResource(R.drawable.main_listview_feature_img);
//		}else if(read == "unread" ) {
//			viewHolder.main_listview_feature_img.setImageResource(R.drawable.unread);
//		}else if(read == "" ){
//			viewHolder.main_listview_feature_img.setImageResource(R.drawable.read);
//		}
//		
		
		if(read.equals("read")){
			viewHolder.main_listview_feature_img.setImageResource(R.drawable.main_listview_feature_img);
		}else if(read.equals("unread")) {
			viewHolder.main_listview_feature_img.setImageResource(R.drawable.mail_unread);
		}else if(read.equals("")){
			viewHolder.main_listview_feature_img.setImageResource(R.drawable.mail_read);
		}
		
		
		viewHolder.main_listview_name.setText((String) mData.get(position).get(
				"subject"));
		
		String sendDate = (String) mData.get(position).get(
				"sendDate");
		long unixTime = TimeUtil.parseTime(sendDate);
		sendDate = TimeUtil.converTimeForMail(unixTime);
		
		viewHolder.main_listview_time.setText(sendDate );
		viewHolder.main_listview_summary.setText((String) mData.get(position)
				.get("fromWho"));
//		viewHolder.main_listview_tips.setText("1");

		viewHolder.main_listview_row
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						Bundle bundle = new Bundle();
						bundle.putString("messageId", (String) mData
								.get(position).get("messageId"));
						
						bundle.putString("folder", (String) mData
								.get(position).get("folder"));
						bundle.putInt("uid", (Integer) mData.get(position).get("uid"));
						

						 Intent mainIntent = new
						 Intent(context,MailContentActivity.class);
						 mainIntent.putExtras(bundle);						 
						 context.startActivity(mainIntent);

					}
				});
		return convertView;
	}

	final class MenuViewHolder {
		public LinearLayout main_listview_row;
		public ImageView main_listview_feature_img;
		public TextView main_listview_name;
		public TextView main_listview_time;
		public TextView main_listview_summary;
		public ImageView main_listview_tips;
	}
}