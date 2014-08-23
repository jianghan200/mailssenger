package com.mailssenger.db;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.mailssenger.model.MsgModel;

/**
 * 
 * @author Han
 * @date 20140823
 */
public class MsgDB extends BaseDB {
	private static String TAG = ">MsgDB "; 
		
	public MsgDB(Context mContext) {
		super(mContext);
	}
	
	public List<MsgModel> getAllMsgWith(String hisEmail) {
		List<MsgModel> msgModels = null;
		try {
			
			//TODO 这里没有考虑和自己聊天的情况
			msgModels = db.findAll(Selector.from(MsgModel.class)
					.where("sender", "=", hisEmail).or("receiver","=",hisEmail));
			Log.i(TAG, "msgModels"+msgModels.size());
				
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(msgModels!=null){
			return msgModels;
		}else{
			return new ArrayList<MsgModel>();
		}
		
	}
	
//	public List<MessageModel> getMsg(String email, int pager) {
//		List<MessageModel> mailist = null;
//		try {
//			mailist = db.findAll(Selector.from(MessageModel.class).where("email", "=", email).offset(pager*10).limit(10));
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return mailist;
//	}

	public int getNewCount(String email) {
		int count = 0;
		try {
			count = (int) db.count(Selector.from(MsgModel.class).where("isNew", "=", 1).and("sender", "=", email));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count ;
	}

	
	/**
	 * 将一个用户的 新属性 全部设置为 旧
	 * @param email
	 */
	public void clearNewCount(String email) {
		
		List<MsgModel> msgList = new ArrayList<MsgModel>();
		
		try {
			 msgList = db.findAll(Selector.from(MsgModel.class).where("isNew", "=", 1).and("sender", "=", email));
		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if(msgList!=null&&msgList.size()>0){
			for (MsgModel messageModel : msgList) {
				messageModel.setNew(false);
			}
			updateAll(msgList);
		}

	}
	
}
