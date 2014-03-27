package com.mailssenger.db;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mailssenger.CommonApplication;
import com.mailssenger.model.MessageModel;
import com.mailssenger.util.L;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageDB {
	public static final String MSG_DBNAME = CommonApplication.DB_NAME;
	
	private SQLiteDatabase db;

	public MessageDB(Context context) {

		db = context.openOrCreateDatabase(MSG_DBNAME, Context.MODE_PRIVATE,
				null);
	}
	
	private String getTableName(String email){
		email = email.replace("@", "_AT_");
		email = email.replace(".", "_");
		for(int i=0;i< email.length();i++){
			if(!Character.isLetterOrDigit(email.charAt(i))){
				email = email.replace(email.charAt(i), (char) 48);//ASCII 0
			}
		}
	
		return "_" + email;
	}
	
	private void createTableIfNotExist(String email){
		
		db.execSQL("CREATE table IF NOT EXISTS "
				+ getTableName(email)
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT, msgType TEXT,message TEXT,time TEXT,isCome TEXT,isNew TEXT)");
	}
	
	
//	private String email;// 消息来自  //邮箱地址
//	private int msgType;// 消息类型
//	private String message;// 消息内容
//	private long time;// 消息日期 //消息发送时间
//	private boolean isComMeg = true;// 是否为收到的消息
//	private boolean isNew;	//是否为新消息

	public void saveMsg(String email, MessageModel entity) {
		createTableIfNotExist(email);
		
		int isCome = 0;
		if (entity.isCome()) {// 如果是收到的消息，保存在数据库的值为1
			isCome = 1;
		}
		db.execSQL(
				"insert into "
							+ getTableName(email)
							+ "(email,msgType,message,time,isCome,isNew) values(?,?,?,?,?,?)",
				new Object[] { entity.getEmail(), entity.getMsgType(),entity.getMessage(),
						entity.getTime(), isCome, entity.isNew() });
	}

	public List<MessageModel> getMsg(String email, int pager) {
		
		createTableIfNotExist(email);
		
		List<MessageModel> list = new LinkedList<MessageModel>();
		
		int num = 10 * (pager + 1);// 本来是准备做滚动到顶端自动加载数据
		
		Cursor c = db.rawQuery("SELECT * from " + getTableName(email) 
				+ " ORDER BY _id DESC LIMIT " + num, null);
		while (c.moveToNext()) {
			
			int msgType = c.getInt(c.getColumnIndex("msgType"));
			String message = c.getString(c.getColumnIndex("message"));
			long time = c.getLong(c.getColumnIndex("time"));
			int isComeMsg = c.getInt(c.getColumnIndex("isCome"));
			int isNewMsg = c.getInt(c.getColumnIndex("isNew"));
			boolean isCome = isComeMsg == 1? true: false;
			boolean isNew = isNewMsg == 1? true: false;
			
			MessageModel entity = new MessageModel(email, msgType, message, time, isCome, isNew);
			
			list.add(entity);
		}
		c.close();
		Collections.reverse(list);// 前后反转一下消息记录
		return list;
	}

	public int getNewCount(String email) {
		
		createTableIfNotExist(email);
		
		Cursor c = db.rawQuery("SELECT isNew from " + getTableName(email)
				+ " WHERE isNew=1",
				null);
		int count = c.getCount();
		// L.i("new message num = " + count);
		c.close();
		return count;
	}

	public void clearNewCount(String email) {
		
		createTableIfNotExist(email);
		
		db.execSQL("update " + getTableName(email) + " set isNew=0"
				+ " WHERE isNew=1");
	}
	

	public void close() {
		if (db != null)
			db.close();
	}
}
