package com.mailssenger.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

/**
 * @date 20140823
 * @author Han
 *
 */
public class ConvModel implements Comparable<ConvModel> {
	
	@Id(column = "email")
	private String email;
	
	@Column(column = "avatar")
	private String avatar;// 头像
	
	@Column(column = "name")
	private String name;// 消息来自
	
	@Column(column = "message")
	private String message;// 消息内容
	
	@Column(column = "newNum")
	private int newNum;// 新消息数目
	
	@Column(column = "time")
	private long time;// 消息日期


	public ConvModel() {
	}

	public ConvModel(String email, String avatar, String name, String message,
			int newNum, long time) {
		super();
		this.email = email;
		this.avatar = avatar;
		this.name = name;
		this.message = message;
		this.newNum = newNum;
		this.time = time;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setHeadImg(String avatar) {
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getNewNum() {
		return newNum;
	}

	public void setNewNum(int newNum) {
		this.newNum = newNum;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	@Override
	public int hashCode() {
		int code = 0;
		code = (31 * (this.email.hashCode())) >> 2;
		return code;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o instanceof ConvModel) {
			ConvModel item = (ConvModel) o;
			if (item.email.equals(this.email))
				return true;
		}
		return false;
	}

	@Override
	public int compareTo(ConvModel another) {
		// TODO Auto-generated method stub
		return (int) (another.time - this.time);
	}

}
