package com.mailssenger.adapter;

import com.mailssenger.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.activity.MailContentActivity;
import com.mailssenger.activity.UserInfoActivity;
import com.mailssenger.model.MailModel;
import com.mailssenger.model.MessageModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.T;
import com.mailssenger.util.TimeUtil;



public class MessageAdapter extends BaseAdapter {
	
	MailModel mailModel;

	public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");

	private Context mContext;
	private LayoutInflater mInflater;
	private List<MessageModel> mMsgList;
	private SharedPreferencesUtil mSpUtil;
	private Gson mGson;
	
	private UserModel hisUserModel;
	
    public static final int TYPE_TEXT_LEFT = 0;//7种不同的布局  
    public static final int TYPE_TEXT_RIGHT = 1;  
    public static final int TYPE_MAIL_LEFT = 2;  
    public static final int TYPE_MAIL_RIGHT = 3;  


	public MessageAdapter(Context context, List<MessageModel> msgList,UserModel hisUserModel) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mMsgList = msgList;
		this.hisUserModel = hisUserModel;
		mInflater = LayoutInflater.from(context);
		mSpUtil = CommonApplication.getInstance().getSpUtil();
		mGson = CommonApplication.getInstance().getGson();
	}

	public void removeHeadMsg() {
		L.i("before remove mMsgList.size() = " + mMsgList.size());
		if (mMsgList.size() - 10 > 10) {
			for (int i = 0; i < 10; i++) {
				mMsgList.remove(i);
			}
			notifyDataSetChanged();
		}
		L.i("after remove mMsgList.size() = " + mMsgList.size());
	}

	public void setMessageList(List<MessageModel> msgList) {
		mMsgList = msgList;
		notifyDataSetChanged();
	}

	public void upDateMsg(MessageModel msg) {
		mMsgList.add(msg);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MessageModel item = mMsgList.get(position);
		boolean isCome = item.isCome();
		int msgType = item.getMsgType();
		ViewHolder holder;
		
		if(msgType == MessageModel.MESSAGE_TYPE_MAIL){
			convertView = mInflater.inflate(R.layout.chat_item_left_mail, null);
			holder = new ViewHolder();
			holder.head = (ImageView) convertView.findViewById(R.id.icon);
			holder.time = (TextView) convertView.findViewById(R.id.datetime);
			
			holder.message_body = (RelativeLayout)convertView.findViewById(R.id.message_body);
			
			holder.message_title = (TextView) convertView.findViewById(R.id.message_title);
			holder.message_content = (TextView) convertView.findViewById(R.id.message_content);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
			convertView.setTag(R.drawable.ic_launcher + position);
			
			holder.time.setText(TimeUtil.getChatTime(item.getTime()));
			// L.i("time: " + item.getDate());
			holder.time.setVisibility(View.VISIBLE);

			holder.head.setImageResource(CommonApplication.heads[item.getEmail().charAt(1)%18]);//头像设置
			if ( !mSpUtil.getShowHead()) {
				holder.head.setVisibility(View.GONE);
			}
			
			String msg = item.getMessage();
			mailModel = CommonApplication.getInstance().getGson().fromJson(msg, MailModel.class);
			holder.message_title.setText(mailModel.getSubject());
			
			String mail_content = mailModel.getContent();
			if(mail_content!=null&&!mail_content.equals("")&&mail_content.length()>90){
				mail_content =  mail_content.substring(0, 90);
			}
			holder.message_content.setText(mail_content);
			
			holder.progressBar.setVisibility(View.GONE);
			holder.progressBar.setProgress(50);
			
			
			holder.message_body
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					MessageModel item = (MessageModel) getItem(position);
					Bundle bundle = new Bundle();
					bundle.putString("mail",item.getMessage());
					
					 Intent mainIntent = new
					 Intent(mContext,MailContentActivity.class);
					 mainIntent.putExtras(bundle);						 
					 mContext.startActivity(mainIntent);

				}
			});
			
			holder.head.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					String hisUserModelString = mGson.toJson(hisUserModel);
					T.dShowLong(mContext, hisUserModelString);
					//将用户对象传给 ChatActivity.class
					Intent intent = new Intent(mContext,UserInfoActivity.class);
					intent.putExtra("user", hisUserModelString);
					mContext.startActivity(intent);

				}
			});
			
		}
		if(msgType == MessageModel.MESSAGE_TYPE_TEXT){
			
				if (convertView == null|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
					
					holder = new ViewHolder();
					if (isCome) {
						convertView = mInflater.inflate(R.layout.chat_item_left, null);
					} else {
						convertView = mInflater.inflate(R.layout.chat_item_right, null);
					}
					
					holder.head = (ImageView) convertView.findViewById(R.id.icon);
					holder.time = (TextView) convertView.findViewById(R.id.datetime);
					holder.msg = (TextView) convertView.findViewById(R.id.textView2);
					holder.progressBar = (ProgressBar) convertView
							.findViewById(R.id.progressBar1);
					convertView.setTag(R.drawable.ic_launcher + position);
				} else {
					holder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
							+ position);
				}
				holder.time.setText(TimeUtil.getChatTime(item.getTime()));
				// L.i("time: " + item.getDate());
				holder.time.setVisibility(View.VISIBLE);
	//			holder.head.setBackgroundResource(CommonApplication.heads[item
	//					.getHeadImg()]);
				holder.head.setImageResource(CommonApplication.heads[item.getEmail().charAt(1)%18]);//头像设置
				if ( !mSpUtil.getShowHead()) {
					holder.head.setVisibility(View.GONE);
				}
	
				holder.msg.setText(
						convertNormalStringToSpannableString(item.getMessage()),
						BufferType.SPANNABLE);
				holder.progressBar.setVisibility(View.GONE);
				holder.progressBar.setProgress(50);
				
				
				holder.head.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String hisUserModelString = mGson.toJson(hisUserModel);
						T.dShowLong(mContext, hisUserModelString);
						//将用户对象传给 ChatActivity.class
						Intent intent = new Intent(mContext,UserInfoActivity.class);
						intent.putExtra("user", hisUserModelString);
						mContext.startActivity(intent);

					}
				});
		}
		
		return convertView;
	}

	/**
	 * 另外一种方法解析表情
	 * 
	 * @param message
	 *            传入的需要处理的String
	 * @return
	 */
	private CharSequence convertNormalStringToSpannableString(String message) {
		// TODO Auto-generated method stub
		String hackTxt;
		if (message.startsWith("[") && message.endsWith("]")) {
			hackTxt = message + " ";
		} else {
			hackTxt = message;
		}
		SpannableString value = SpannableString.valueOf(hackTxt);

		Matcher localMatcher = EMOTION_URL.matcher(value);
		while (localMatcher.find()) {
			String str2 = localMatcher.group(0);
			int k = localMatcher.start();
			int m = localMatcher.end();
			// k = str2.lastIndexOf("[");
			// Log.i("way", "str2.length = "+str2.length()+", k = " + k);
			// str2 = str2.substring(k, m);
			if (m - k < 8) {
				if (CommonApplication.getInstance().getFaceMap()
						.containsKey(str2)) {
					int face = CommonApplication.getInstance().getFaceMap()
							.get(str2);
					Bitmap bitmap = BitmapFactory.decodeResource(
							mContext.getResources(), face);
					if (bitmap != null) {
						ImageSpan localImageSpan = new ImageSpan(mContext,
								bitmap, ImageSpan.ALIGN_BASELINE);
						value.setSpan(localImageSpan, k, m,
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return value;
	}

	static class ViewHolder {
		ImageView head;
		TextView time;
		TextView msg;
		
		RelativeLayout message_body;
		TextView message;
		TextView message_title;
		TextView message_content;
		
		ImageView imageView;
		ProgressBar progressBar;

	}
}