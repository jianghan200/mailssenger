package com.mailssenger.db;

import com.mailssenger.CommonApplication;
import com.mailssenger.model.UserModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHelper extends SQLiteOpenHelper{
	public static final String USER_DBNAME = CommonApplication.DB_NAME;
	
	public UserDBHelper(Context context) {
		super(context, USER_DBNAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE table IF NOT EXISTS user"
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, userId TEXT, channelId TEXT, "
				+ "nickName TEXT, emotion TEXT, avatar TEXT, region TEXT, sex TEXT,userType TEXT)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE user ADD COLUMN other TEXT");
	}
	
//	private String email;//全局唯一 用户邮箱地址
//	private String userId;//
//	private String channelId;//
//	private String nickName;//昵称
//	private String emotion;//心情
//	private String avatar;//头像
//	private String region;
//	private int sex;
}
