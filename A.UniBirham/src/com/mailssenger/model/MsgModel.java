package com.mailssenger.model;


import com.google.gson.annotations.Expose;
import com.lidroid.xutils.db.annotation.Column;

/**
 * @date 20140823
 */
public class MsgModel extends EntityBase {
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
	
	//都不是主键，主键由系统自动生成
	@Expose
	@Column(column = "receiver")
	private String receiver;
	
	@Expose
	@Column(column = "sender")
	private String sender;
	
	@Expose
	@Column(column = "msgType")
	private int msgType;// 消息类型
	@Expose
	@Column(column = "message")
	private String message;// 消息内容
	@Expose
	@Column(column = "time")
	private long time;// 消息日期 //消息发送时间
	

	@Column(column = "isNew")
	private boolean isNew;	//是否为新消息
	
	//如果为邮件的话,message为邮件的ID
	//如果为图像,mesaage为图像的地址
	//如果为音频,mesaage为音频
	
	//通过邮箱地址获得一个friend 对象, friend可能使用APP
	//也可能没使用APP
	//通过是否有userID来判断


	public MsgModel() {

		this.isNew = false;//默认是已阅信息
	}

	public MsgModel(String sender,String receiver, int msgType, String message,
			 long date) {
		super();
		this.sender= sender;
		this.receiver = receiver;
		this.msgType = msgType;
		this.message = message;
		this.time = date;
		this.isNew = false;//默认是已阅信息
	}
	
	public MsgModel(String sender,String receiver, int msgType, String message,
			 long date,boolean isNew) {
		super();
		this.sender= sender;
		this.receiver = receiver;
		this.msgType = msgType;
		this.message = message;
		this.time = date;
		this.isNew = isNew;
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

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public String toString() {
		return "ChatMessageItem [sender=" + sender +",receiver="+ receiver +", msgType=" + msgType
				+ ", message=" + message + ", time=" + time + "]";
	}


}
