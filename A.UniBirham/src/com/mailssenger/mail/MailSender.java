package com.mailssenger.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.mailssenger.CommonApplication;



import android.util.Log;

public class MailSender extends javax.mail.Authenticator {
//	private String mailhost = "smtp.gmail.com";
	
	private String mailhost = "";
	private String user;
	private String password;
	private Session session;

	private Multipart _multipart; // for attachment

	/*
	 * for attachment
	 */
	public void addAttachment(String filename) throws Exception {
		 System.out.println("::addAttachment::  filename is "+filename);
		 filename=filename.replace("file://","");
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		_multipart.addBodyPart(messageBodyPart);
	}

	static {
		Security.addProvider(new JSSEProvider());
	}

	/*
	 * constructor
	 */
	public MailSender(String user, String password) {
		this.user = user;
		this.password = password;
		
		mailhost =CommonApplication.SMTP_HOST;
		
		//set up the properties
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", mailhost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		session = Session.getDefaultInstance(props, this);
	}

	/*
	 * PasswordAuthentication 
	 * (non-Javadoc)
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 */
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}
//	subject,message,sendAddr,email,ccAddr,attachment
	public synchronized void sendMail(String subject, String body,
			String sender,
			String recipients,
			String ccRecipients,String attachment) throws Exception {
		
		//debug info
		System.out.println("In ::SendMail:: function");
		System.out.println("sendAddr is "+sender );
        System.out.println("Recipients is "+recipients);
        System.out.println("ccRecipients is "+ccRecipients);
        System.out.println("subject is "+subject);
        System.out.println("body is "+body);
        System.out.println("attachment is "+attachment);
        
		try {
			
			Log.e("SendMail","I am trying to send message"); 
			MimeMessage message = new MimeMessage(session);
			DataHandler handler = new DataHandler(new ByteArrayDataSource(
					body.getBytes(), "text/plain"));
			
			message.setFrom(new InternetAddress(sender));
			message.setSubject(subject);
			message.setDataHandler(handler);

			
			if(attachment!=null&&attachment!=""){
				_multipart = new MimeMultipart();// for attachment
				addAttachment(attachment);
				message.setContent(_multipart);// for attachment
			}
			
//			addAttachment("/mnt/sdcard/0han/4.png","文件subject2？");
//			addAttachment("/mnt/sdcard/0han/Media Musics/adele - rolling in the deep - 小萝莉翻唱 的.mp3","文件subject2？");

			//for recipients
			if (recipients.indexOf(',') > 0)
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipients));
			else
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(recipients));
			
			//for cc
			if(!ccRecipients.equals("")){
				
				if (ccRecipients.indexOf(',') > 0)
					message.setRecipients(Message.RecipientType.CC,
							InternetAddress.parse(ccRecipients));
				else
					message.setRecipient(Message.RecipientType.CC,
							new InternetAddress(ccRecipients));
			}
			
			
			//send the message
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SendMail","Sender exception"); 
		}
	}

	public class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}
	}
}