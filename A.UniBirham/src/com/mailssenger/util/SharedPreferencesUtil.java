package com.mailssenger.util;

import com.mailssenger.CommonApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
	public static final String MESSAGE_NOTIFY_KEY = "message_notify";
	public static final String MESSAGE_SOUND_KEY = "message_sound";
	public static final String SHOW_HEAD_KEY = "show_head";
	public static final String PULLREFRESH_SOUND_KEY = "pullrefresh_sound";
	

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharedPreferencesUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	
//	CommonApplication.ACCOUNT = userData.getString("userAccount", "");
//	CommonApplication.PASSWORD = userData.getString("userPassword", "");
//	CommonApplication.SACCOUNT = userData.getString("userSAccount", "");
//	CommonApplication.IMAP_HOST = userData.getString("userIMAP_HOST", "");
//	CommonApplication.SMTP_HOST = userData.getString("userSMTP_HOST", "");
	
	//first time login flag
	public void setLogin(boolean isLogin){
		editor.putBoolean("isLogin", isLogin);
		editor.commit();
	}
	
	public boolean isLogin() {
		return sp.getBoolean("isLogin", false);
	}
	
	
	//first time login flag
	public void setFirst(boolean isFirst){
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}
	
	public boolean isFirst() {
		return sp.getBoolean("isFisrt", true);
	}
	
	
	//账户系统
	public void setEmail(String email){
		
		editor.putString("email", email);
		editor.commit();
		
	}
	
	public String getEmail() {
		return sp.getString("email", "");
	}
	
	public void setPassword(String password){
		
		editor.putString("password", password);
		editor.commit();
		
	}
	
	public String getPassword() {
		return sp.getString("password", "");
	}
	
	// appid
	public void setAppId(String appid) {
		// TODO Auto-generated method stub
		editor.putString("appid", appid);
		editor.commit();
	}

	public String getAppId() {
		return sp.getString("appid", "");
	}

	// user_id
	public void setUserId(String userId) {
		editor.putString("userId", userId);
		editor.commit();
	}

	public String getUserId() {
		return sp.getString("userId", "");
	}

	// channel_id
	public void setChannelId(String ChannelId) {
		editor.putString("ChannelId", ChannelId);
		editor.commit();
	}

	public String getChannelId() {
		return sp.getString("ChannelId", "");
	}

	// nick
	public void setNick(String nick) {
		editor.putString("nick", nick);
		editor.commit();
	}

	public String getNick() {
		return sp.getString("nick", "");
	}



	// 设置Tag
	public void setTag(String tag) {
		editor.putString("tag", tag);
		editor.commit();
	}

	public String getTag() {
		return sp.getString("tag", "");
	}

	// 是否通知
	public boolean getMsgNotify() {
		return sp.getBoolean(MESSAGE_NOTIFY_KEY, true);
	}

	public void setMsgNotify(boolean isChecked) {
		editor.putBoolean(MESSAGE_NOTIFY_KEY, isChecked);
		editor.commit();
	}

	// 新消息是否有声音
	public boolean getMsgSound() {
		return sp.getBoolean(MESSAGE_SOUND_KEY, true);
	}

	public void setMsgSound(boolean isChecked) {
		editor.putBoolean(MESSAGE_SOUND_KEY, isChecked);
		editor.commit();
	}

	// 刷新是否有声音
	public boolean getPullRefreshSound() {
		return sp.getBoolean(PULLREFRESH_SOUND_KEY, true);
	}

	public void setPullRefreshSound(boolean isChecked) {
		editor.putBoolean(PULLREFRESH_SOUND_KEY, isChecked);
		editor.commit();
	}

	// 是否显示自己头像
	public boolean getShowHead() {
		return sp.getBoolean(SHOW_HEAD_KEY, true);
	}

	public void setShowHead(boolean isChecked) {
		editor.putBoolean(SHOW_HEAD_KEY, isChecked);
		editor.commit();
	}
	
	
	//邮箱系统相关, SMTP 与 IMAP 验证
	public String getMailServerAccount() {
		return sp.getString("mailServerAccount", "");
	}


	public void setMailServerAccount(String mailServerAccount) {
		editor.putString("mailServerAccount", mailServerAccount);
		editor.commit();
	}


	public String getMailServerPassword() {
		return sp.getString("mailServerPassword", "");
	}


	public void setMailServerPassword(String mailServerPassword) {
		editor.putString("mailServerPassword", mailServerPassword);
		editor.commit();
	}


	public String getIMAPHost() {
		return sp.getString("IMAPHost", "");
	}


	public void setIMAPHost(String IMAPHost) {
		editor.putString("IMAPHost", IMAPHost);
		editor.commit();
	}


	public String getSMTPHost() {
		return sp.getString("SMTPHost", "");
	}

	public void setSMTPHost(String SMTPHost) {
		editor.putString("SMTPHost", SMTPHost);
		editor.commit();
	}

}
