package com.mailssenger.util;

import android.os.AsyncTask;
import android.os.Handler;

import com.mailssenger.CommonApplication;
import com.mailssenger.R;
import com.mailssenger.mail.MailAccount;


public class LoginAsyncTask {
	
	private Handler mHandler;
	private MyAsyncTask mTask;
	private String mUserId;
	private OnLoginScuessListener mListener;

	public interface OnLoginScuessListener {
		void sendScuess();
	}

	public void setOnSendScuessListener(OnLoginScuessListener listener) {
		this.mListener = listener;
	}

	Runnable reSend = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			L.i("resend msg...");
			send();//重发
		}
	};

	public LoginAsyncTask(String jsonMsg,String useId) {
		// TODO Auto-generated constructor stub

		mUserId = useId;
		mHandler = new Handler();
	}

	// 发送
	public void send() {
		if (NetUtil.isNetConnected( CommonApplication.getInstance())) {//如果网络可用
			mTask = new MyAsyncTask();
			mTask.execute();
		} else {
			T.showLong( CommonApplication.getInstance(), R.string.net_error_tip);
			
		}
	}

	// 停止
	public void stop() {
		if (mTask != null)
			mTask.cancel(true);
	}

	class MyAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... message) {
			String result = "";
			 
			MailAccount mail = new MailAccount();
			 
			 if(mail.authentication()){
				 return "succeed!";
			 }else{
				 return "failed";
			 }
			/*if(TextUtils.isEmpty(mUserId))
				result = mBaiduPush.PushMessage(mMessage);
			else
				result = mBaiduPush.PushMessage(mMessage, mUserId);*/
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			L.i("send msg result:"+result);
			if (result.equals("failed")) {// 如果消息发送失败，则100ms后重发
				mHandler.postDelayed(reSend, 100);
			} else {
				if (mListener != null)
					mListener.sendScuess();
			}
		}
	}
}
