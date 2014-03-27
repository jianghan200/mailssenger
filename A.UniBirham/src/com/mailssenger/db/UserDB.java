package com.mailssenger.db;



import java.util.LinkedList;
import java.util.List;

import com.mailssenger.model.UserModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDB {
	private UserDBHelper helper;

	public UserDB(Context context) {
		helper = new UserDBHelper(context);
	}

//	private String email;//全局唯一 用户邮箱地址
//	private String userId;//
//	private String channelId;//
//	private String nickName;//昵称
//	private String emotion;//心情
//	private String avatar;//头像
//	private String region;
//	private int sex;
	
	public UserModel selectInfo(String email) {
		UserModel u = new UserModel();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from user where email=?",
				new String[] { email + "" });
		if (c.moveToFirst()) {
			
			u.setEmail(c.getString(c.getColumnIndex("email")));
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setNickName((c.getString(c.getColumnIndex("nickName"))));
			u.setEmotion((c.getString(c.getColumnIndex("emotion"))));
			u.setAvatar((c.getString(c.getColumnIndex("avatar"))));
			u.setRegion((c.getString(c.getColumnIndex("region"))));
			u.setSex((c.getInt(c.getColumnIndex("sex"))));
			
		} else {
			return null;
		}
		return u;
	}

	public void addUser(List<UserModel> list) {
		SQLiteDatabase db = helper.getWritableDatabase();
		for (UserModel u : list) {
			db.execSQL(
					"insert into user (email,userId,channelID,nickName,emotion,avatar,region,sex) values(?,?,?,?,?,?,?,?)",
					new Object[] { u.getEmail(),u.getUserId(),u.getChannelId(), u.getNickName(),
							u.getEmotion(),u.getAvatar(), u.getRegion(),u.getSex() });
		}
		db.close();
	}

	public void addUser(UserModel u) {
		if (selectInfo(u.getUserId()) != null) {
			update(u);
			return;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"insert into user (email,userId,channelID,nickName,emotion,avatar,region,sex) values(?,?,?,?,?,?,?,?)",
				new Object[] { u.getEmail(),u.getUserId(),u.getChannelId(), u.getNickName(),
						u.getEmotion(),u.getAvatar(), u.getRegion(),u.getSex() });
		db.close();

	}

	public UserModel getUser(String email) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user where email=?",
				new String[] { email });
		UserModel u = new UserModel();
		if (c.moveToNext()) {
			
			u.setEmail(c.getString(c.getColumnIndex("email")));
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setNickName((c.getString(c.getColumnIndex("nickName"))));
			u.setEmotion((c.getString(c.getColumnIndex("emotion"))));
			u.setAvatar((c.getString(c.getColumnIndex("avatar"))));
			u.setRegion((c.getString(c.getColumnIndex("region"))));
			u.setSex((c.getInt(c.getColumnIndex("sex"))));
		}
		return u;
	}

	public void updateUser(List<UserModel> list) {
		if (list.size() > 0) {
			delete();
			addUser(list);
		}
	}

	public List<UserModel> getUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<UserModel> list = new LinkedList<UserModel>();
		Cursor c = db.rawQuery("select * from user", null);
		while (c.moveToNext()) {
			UserModel u = new UserModel();
			
			u.setEmail(c.getString(c.getColumnIndex("email")));
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setNickName((c.getString(c.getColumnIndex("nickName"))));
			u.setEmotion((c.getString(c.getColumnIndex("emotion"))));
			u.setAvatar((c.getString(c.getColumnIndex("avatar"))));
			u.setRegion((c.getString(c.getColumnIndex("region"))));
			u.setSex((c.getInt(c.getColumnIndex("sex"))));
			
			list.add(u);
		}
		c.close();
		db.close();
		return list;
	}

	public void update(UserModel u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"update user set userId=?,channelID=?,nickName=?,emotion=?,avatar=?,region=?,sex=? where email=?",
				new Object[] { u.getUserId(),u.getChannelId(), u.getNickName(),
						u.getEmotion(),u.getAvatar(), u.getRegion(),u.getSex(), u.getEmail()});

		db.close();
	}

	public UserModel getLastUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user", null);
		UserModel u = new UserModel();
		while (c.moveToLast()) {
			u.setEmail(c.getString(c.getColumnIndex("email")));
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setNickName((c.getString(c.getColumnIndex("nickName"))));
			u.setEmotion((c.getString(c.getColumnIndex("emotion"))));
			u.setAvatar((c.getString(c.getColumnIndex("avatar"))));
			u.setRegion((c.getString(c.getColumnIndex("region"))));
			u.setSex((c.getInt(c.getColumnIndex("sex"))));
		}
		c.close();
		db.close();
		return u;
	}

	public void delUser(UserModel u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user where email=?",
				new Object[] { u.getEmail() });
		db.close();
	}

	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user");
		db.close();
	}
}
