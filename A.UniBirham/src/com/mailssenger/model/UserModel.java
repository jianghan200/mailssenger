package com.mailssenger.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class UserModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	private String email;//全局唯一 用户邮箱地址
	@Expose
	private String userId;//
	@Expose
	private String channelId;//
	@Expose
	private String nickName;//昵称
	@Expose
	private String emotion;//心情
	@Expose
	private String avatar;//头像
	@Expose
	private String region;
	@Expose
	private int sex;
	@Expose
	private String userType;
	
	
	public UserModel(String email, String userId, String channelId, String nickName, String emotion,
			String avatar,	String region,	int sex) {
		// TODO Auto-generated constructor stub
		this.email = email;
		this.userId = userId;
		this.channelId = channelId;
		this.nickName = nickName;
		this.emotion = emotion;
		this.avatar = avatar;
		this.region = region;
		this.sex = sex;
		this.userType = "friends";
	}

	public UserModel() {

	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@Override
	public String toString() {
		return "UserModel [email=" + email + ", userId=" + userId
				+ ", channelId=" + channelId + ", nickName=" + nickName
				+ ", emotion=" + emotion + ", avatar=" + avatar + ", region="
				+ region + ", sex=" + sex + ", userType=" + userType + "]";
	}




}
