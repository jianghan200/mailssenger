package com.mailssenger.util;


import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.model.MailModel;
import com.mailssenger.model.MessageModel;
import com.mailssenger.model.UserModel;

public class DbUtil {
	
	CommonApplication mApplication = CommonApplication.getInstance();
	SharedPreferencesUtil mSpUtil = mApplication.getSpUtil();
	Gson mGson = mApplication.getGson();
	UserDB mUserDB = mApplication.getUserDB();
	MessageDB mMsgDB = mApplication.getMessageDB();
	RecentDB mRecentDB = mApplication.getRecentDB();
	
	public static void saveMail(MailModel mail){
		
		
	}
	
	public void sample(){
		
		String hisEmail = "";
		UserModel hisUserModel = mUserDB.selectInfo(hisEmail);
		
		MessageModel chatMessage = new MessageModel( mSpUtil.getEmail(),
				MessageModel.MESSAGE_TYPE_TEXT, "content" ,System.currentTimeMillis());

		//存信息,为自己发的信息,email 设为对方的地址,isCome 为false
		mMsgDB.saveMsg(hisUserModel.getEmail(), chatMessage);
		
		
		ConversationModel recentItem = new ConversationModel();
		recentItem.setEmail(hisEmail);
		recentItem.setName(hisUserModel.getNickName());
		recentItem.setMessage(chatMessage.getMessage());
		recentItem.setNewNum(0);
		recentItem.setTime(System.currentTimeMillis());

		mRecentDB.saveRecent(recentItem);
	}
	
}
