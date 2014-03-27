package com.mailssenger.model;


import java.io.Serializable;

import android.R.bool;

import com.google.gson.annotations.Expose;


public class MessageModel {
	// Text
	public static final int MESSAGE_TYPE_TEXT = 1;
	// image
	public static final int MESSAGE_TYPE_IMG = 2;
	// file
	public static final int MESSAGE_TYPE_FILE = 3;
	// image
	public static final int MESSAGE_TYPE_MAIL = 4;
	
	//
	public static final int MESSAGE_TYPE_FRIEND_REQUEST = 10;
	public static final int MESSAGE_TYPE_GROUP_REQUEST = 11;
	
	public static final int MESSAGE_TYPE_NEW_USER = 12;
	public static final int MESSAGE_TYPE_NEW_USER_RESPONSE = 13;
	
	@Expose
	private String email;// 消息来自  //邮箱地址
	@Expose
	private int msgType;// 消息类型
	@Expose
	private String message;// 消息内容
	@Expose
	private long time;// 消息日期 //消息发送时间
	
	private boolean isCome;// 是否为收到的消息
	private boolean isNew;	//是否为新消息
	
	//如果为邮件的话,message为邮件的ID
	//如果为图像,mesaage为图像的地址
	//如果为音频,mesaage为音频
	
	//通过邮箱地址获得一个friend 对象, friend可能使用APP
	//也可能没使用APP
	//通过是否有userID来判断


	public MessageModel() {
		// TODO Auto-generated constructor stub
	}

	public MessageModel(String email, int msgType, String message,
			 long date) {
		super();
		this.email = email;
		this.msgType = msgType;
		this.message = message;
		this.time = date;
		this.isCome = false;
		this.isNew = false;
	}
	
	public MessageModel(String email, int msgType, String message,
			 long date,boolean isCome,boolean isNew) {
		super();
		this.email = email;
		this.msgType = msgType;
		this.message = message;
		this.time = date;
		this.isCome = isCome;
		this.isNew = isNew;
	}


	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isCome() {
		return isCome;
	}

	public void setCome(boolean isCome) {
		this.isCome = isCome;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public String toString() {
		return "ChatMessageItem [email=" + email + ", msgType=" + msgType
				+ ", message=" + message + ", time=" + time + "]";
	}

}
