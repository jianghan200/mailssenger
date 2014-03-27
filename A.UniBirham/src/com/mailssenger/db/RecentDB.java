package com.mailssenger.db;


import java.util.Collections;
import java.util.LinkedList;

import com.mailssenger.CommonApplication;
import com.mailssenger.model.ConversationModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class RecentDB {
	public static final String MSG_DBNAME = CommonApplication.DB_NAME;
	private static final String RECENT_TABLE_NAME = "recent";
	private SQLiteDatabase db;

	public RecentDB(Context context) {
		db = context.openOrCreateDatabase(MSG_DBNAME, Context.MODE_PRIVATE,
				null);
		db.execSQL("CREATE table IF NOT EXISTS "
				+ RECENT_TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT, avatar TEXT, name TEXT,message TEXT, newNum TEXT, time TEXT)");
	
	}

//	private String email;
//	private String avatar;// 头像
//	private String name;// 消息来自
//	private String message;// 消息内容
//	private int newNum;// 新消息数目
//	private long time;// 消息日期
	
	public void saveRecent(ConversationModel item) {
		if (isExist(item.getEmail())) {
			
			ContentValues cv = new ContentValues();
			cv.put("avatar", item.getAvatar());
			cv.put("name", item.getName());
			cv.put("message", item.getMessage());
			cv.put("newNum", item.getNewNum());
			cv.put("time", item.getTime());

			db.update(RECENT_TABLE_NAME, cv, "email=?",
					new String[] { item.getEmail() });
		} else {
			db.execSQL(
					"insert into "
							+ RECENT_TABLE_NAME
							+ " (email,avatar,name,message,newNum,time) values(?,?,?,?,?,?)",
					new Object[] { item.getEmail(), item.getAvatar(), 
							item.getName(),item.getMessage(),
							item.getNewNum(),item.getTime()
							  });
		}
	}

	public LinkedList<ConversationModel> getRecentList() {
		LinkedList<ConversationModel> list = new LinkedList<ConversationModel>();
		Cursor c = db.rawQuery("SELECT * from " + RECENT_TABLE_NAME, null);
		while (c.moveToNext()) {
			String email = c.getString(c.getColumnIndex("email"));
			String avatar = c.getString(c.getColumnIndex("avatar"));
			String name = c.getString(c.getColumnIndex("name"));
			String message = c.getString(c.getColumnIndex("message"));
			int newNum = c.getInt(c.getColumnIndex("newNum"));
			long time = c.getLong(c.getColumnIndex("time"));

			ConversationModel item = new ConversationModel(email, avatar,name, message, newNum,
					time);
			list.add(item);
		}
		Collections.sort(list);// 按时间降序
		return list;
	}

	public void delRecent(String email) {
		db.delete(RECENT_TABLE_NAME, "email=?", new String[] { email });
	}

	private boolean isExist(String email) {
		Cursor c = db.rawQuery("SELECT * FROM " + RECENT_TABLE_NAME
				+ " WHERE email = ?", new String[] { email });
		return c.moveToFirst();
	}

	public void close() {
		if (db != null)
			db.close();
	}
}
