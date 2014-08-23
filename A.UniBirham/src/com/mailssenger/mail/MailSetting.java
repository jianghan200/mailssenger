package com.mailssenger.mail;

import android.R.integer;

public class MailSetting {
	
	String account;
	String password;
	
	String smtpAuthAccount;
	String smtpAuthPassword;
	String smtpAuthAddress;
	int smtpAuthPort;
	
	
	public int getSmtpAuthPort() {
		return smtpAuthPort;
	}

	public void setSmtpAuthPort(int smtpAuthPort) {
		this.smtpAuthPort = smtpAuthPort;
	}

	public int getImapAuthPort() {
		return imapAuthPort;
	}

	public void setImapAuthPort(int imapAuthPort) {
		this.imapAuthPort = imapAuthPort;
	}
	String imapAuthAccount;
	String imapAuthPassword;
	String imapAuthAddress;
	int imapAuthPort;
	
	
	public MailSetting(String account,String password) {
		this.account = account;
		this.password = password;
		smtpAuthAccount = account;
		imapAuthAccount = account;
		smtpAuthPassword = password;
		imapAuthPassword = password;
	}
	
	public MailSetting(String account,String password,String imapAuthAddress,String smtpAuthAddress){
		MailSetting mailSetting = new MailSetting(account,password);
		
		mailSetting.setSmtpAuthAddress(smtpAuthAddress);
		mailSetting.setImapAuthAddress(imapAuthAddress);
	}
	
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSmtpAuthAccount() {
		return smtpAuthAccount;
	}
	public void setSmtpAuthAccount(String smtpAuthAccount) {
		this.smtpAuthAccount = smtpAuthAccount;
	}
	public String getSmtpAuthPassword() {
		return smtpAuthPassword;
	}
	public void setSmtpAuthPassword(String smtpAuthPassword) {
		this.smtpAuthPassword = smtpAuthPassword;
	}
	public String getSmtpAuthAddress() {
		return smtpAuthAddress;
	}
	public void setSmtpAuthAddress(String smtpAuthAddress) {
		this.smtpAuthAddress = smtpAuthAddress;
	}
	public String getImapAuthAccount() {
		return imapAuthAccount;
	}
	public void setImapAuthAccount(String imapAuthAccount) {
		this.imapAuthAccount = imapAuthAccount;
	}
	public String getImapAuthPassword() {
		return imapAuthPassword;
	}
	public void setImapAuthPassword(String imapAuthPassword) {
		this.imapAuthPassword = imapAuthPassword;
	}
	public String getImapAuthAddress() {
		return imapAuthAddress;
	}
	public void setImapAuthAddress(String imapAuthAddress) {
		this.imapAuthAddress = imapAuthAddress;
	}
	
	
	
}
