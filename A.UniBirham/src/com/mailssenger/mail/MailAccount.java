package com.mailssenger.mail;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.activity.MainActivity;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.ConversationModel;
import com.mailssenger.model.MailModel;
import com.mailssenger.model.MessageModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.util.L;
import com.mailssenger.util.SharedPreferencesUtil;
import com.mailssenger.util.TimeUtil;
import com.mailssenger.util.UIHelper;
import com.sun.mail.imap.IMAPFolder;

public class MailAccount {
	
	private String TAG = " >MailAccount";
	
	//basic setting information
	
	private String username = CommonApplication.SACCOUNT;
	private String password = CommonApplication.PASSWORD;
	private String imap_host = CommonApplication.IMAP_HOST;
	private String smtp_host = CommonApplication.SMTP_HOST;
	
	public Session imapSession = null;
	private Store imap_store = null;
	

	Gson mGson =CommonApplication.getInstance().getGson();
	MailAccount(String username, String password) {
		this.username = username;
		this.password = password;
		imap_host = CommonApplication.IMAP_HOST;
		smtp_host = CommonApplication.SMTP_HOST;
	}

	/*
	 * connect and get imapSesion
	 */
	public MailAccount() {
		username = CommonApplication.SACCOUNT;
		password = CommonApplication.PASSWORD;
		imap_host = CommonApplication.IMAP_HOST;
		smtp_host = CommonApplication.SMTP_HOST;

		System.out.println("::MailAccount::");
		System.out.println("username is " + username);
		System.out.println("password is " + password);

		Properties props = new Properties();
		// IMAPS protocol
		props.setProperty("mail.store.protocol", "imaps");
		// Set host address
		props.setProperty("mail.imaps.host", imap_host);
		// Set specified port
		props.setProperty("mail.imaps.port", "993");
		// Using SSL
		props.setProperty("mail.imaps.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.imaps.socketFactory.fallback", "false");
		// Setting IMAP session
		imapSession = Session.getInstance(props);
	}

	public boolean authentication() {

		try {
			Store store = imapSession.getStore("imaps");
			store.connect(username, password);
			//get imap folder
			IMAPFolder inbox = (IMAPFolder) store.getFolder("inbox");
			// open it
			if (!inbox.isOpen()) {
				inbox.open(Folder.READ_ONLY);
			}
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
	}
	/*
	 * get imap store
	 */
	private Store getIMAPStore() {

		Store imapStore = null;
		try {
			//get session
			imapStore = imapSession.getStore("imaps");
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// connect and login
			imapStore.connect(username, password);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imapStore;
	}

	/**
	 * Get IMAP Folder
	 * @param folderName
	 * @param readOnly
	 * @return
	 */ 
	private IMAPFolder openIMAPFolder(String folderName, boolean readOnly) {
		Store store = getIMAPStore();
		IMAPFolder imapFolder = null;

		try {
			imapFolder = (IMAPFolder) store.getFolder(folderName);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!imapFolder.isOpen()) {
			try {
				if (readOnly) {
					//open with read only
					imapFolder.open(Folder.READ_ONLY);
				} else {
					//open with read and write
					imapFolder.open(Folder.READ_WRITE);
				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return imapFolder;
	}

	/*
	 * check new mail from server
	 */
	public void checkNewMailFromServer(String folderName) {
		CommonApplication.debug("I am in checkNewMailFromServer");

		Set<Integer> unread_uid_set = new HashSet<Integer>();
		try {
			//get unread uid set 
			unread_uid_set = getNewUnreadMailSet(folderName);
			
			System.out.println("Unread UID sets is : " + unread_uid_set);

			if (unread_uid_set.size() != 0) {
				//got new mail and download it
				getNewMails(folderName, unread_uid_set);
				
			} else {

				CommonApplication.debug("Notification cancel !: ");
				//toast no new mail
//				UIHelper.toastNoNewMail();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}



	/*
	 * get unread uid set  from the server
	 */
	public Set<Integer> getNewUnreadMailSet(String folderName) {
		CommonApplication.debug("I am getNewUnreadMailNumber!");

		Set<Integer> unread_uid_set = new HashSet<Integer>();
		
		try {
			IMAPFolder inbox = openIMAPFolder(folderName, true);
			
			System.out.println("No of Unread Messages : "
					+ inbox.getUnreadMessageCount());
			//setup search requirement
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Message messages[] = inbox.search(ft);
			
			//get all the uid and make a uid set
			int uid;
			for (int i = 0; i < messages.length; i++) {
				uid = (int) inbox.getUID(messages[i]);
				unread_uid_set.add(uid);
			}
			System.out.println("Unread UID sets is : " + unread_uid_set);
			
			Set<Integer> local_uid_set = CommonApplication.dbOperation.loadUIDSet(folderName);
			// compare and find those is new
			unread_uid_set.removeAll(local_uid_set);
			return unread_uid_set;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}
/*
	 * get the new mail from server
	 */
	public void getNewMails(String folder, Set<Integer> new_uid_set) {
	
		Iterator<Integer> it = new_uid_set.iterator();
		while (it.hasNext()) {
			int uid = it.next();
	
			MailModel mail = new MailModel();
	
			try {
				//get one mail with folder and uid ,without download the content
				
				
				mail = getOneMail(folder, uid, true);
				
				saveInChatDatabase(mail);
				CommonApplication.dbOperation.save(mail);
				
	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
	}

	/*
 * get folder list from server
 */
	public void getFolderListFromServer() {
		try {
			Store store = imapSession.getStore("imaps");
			store.connect(username, password);
			System.out.println(store);

			// get folder list 
			Folder[] f = store.getDefaultFolder().list();
			for (Folder fd : f)
				System.out.println(">> " + fd.getName());

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
/*
 * mark mail As read to server
 */
	public String markMailAsSeenToServer(String folderName, Integer uid) {

		try {
			IMAPFolder imapFolder = openIMAPFolder(folderName, false);

			UIDFolder uFolder = (UIDFolder) imapFolder;
			Message message = uFolder.getMessageByUID(uid);
			//check whether the mail is seen
			Flags mes_flag = message.getFlags();
			//if it is not seen,set it to seen
			if(!mes_flag.contains(Flag.SEEN)){
				imapFolder.setFlags(new Message[] { message }, new Flags(
						Flags.Flag.SEEN), true);
			}

			//markMailAsSeenToLocal

			CommonApplication.debug("I am mark the mail as seen to local");
			CommonApplication.dbOperation.markMailAsSeenToLocal(uid);

			return "yes";
		} catch (Exception e) {

			e.printStackTrace();
			return "no";
		}

	}

	/*
	 * sync folder with the server
	 */
	public String syncMail(String folderName) {
		// mail uid set from server
		Set<Integer> server_uid_set = getUIDSetFromServer(folderName);
		Set<Integer> local_uid_set = CommonApplication.dbOperation.loadUIDSet(folderName);
		// compare and find those is new
		server_uid_set.removeAll(local_uid_set);

		System.out.println("The local UID SET is ");
		System.out.println(local_uid_set);
		System.out.println("The mail that will be download:" + server_uid_set);

		//get the new mail from server
		getNewMails(folderName, server_uid_set);

		return "yes";
	}

	/*
	 * get all mail uid set from the server
	 */
	public Set<Integer> getUIDSetFromServer(String folderName) {

		Set<Integer> latest_uid_set = new HashSet<Integer>();
		try {
			Store store = imapSession.getStore("imaps");
			store.connect(username, password);

			IMAPFolder inbox = (IMAPFolder) store.getFolder(folderName);
			if (!inbox.isOpen()) {
				inbox.open(Folder.READ_ONLY);
			}
			//get all the message
			Message[] messages = inbox.getMessages();
			
			//get the uid set and add it to uid set
			int uid;
			for (int i = 0; i < messages.length; i++) {
				uid = (int) inbox.getUID(messages[i]);
				latest_uid_set.add(uid);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		System.out.println("::getUIDSetFromServer:: " + latest_uid_set);
		return latest_uid_set;
	}

	/*
	 * open UID folder, for authentication
	 */
	public UIDFolder openUIDFolder(String foldername) {

		String folder = foldername;

		try {
			Store store = imapSession.getStore("imaps");
			store.connect(username, password);
			//get imap folder
			IMAPFolder inbox = (IMAPFolder) store.getFolder(folder);
			// open it
			if (!inbox.isOpen()) {
				inbox.open(Folder.READ_ONLY);
			}
			UIDFolder ufolder = (UIDFolder) inbox;

			return ufolder;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;

	}
	/**
	 * send new mail
	 */
	public void sendMail(Bundle bundle) {

		
		//get all the data from bundle
		String username = bundle.getString("username");
		String password = bundle.getString("password");
		String sendAddr = CommonApplication.ACCOUNT;
		String email = bundle.getString("email");
		String ccAddr = bundle.getString("ccAddr");
		// String bccAddr = bundle.getString("bccAddr");
		String subject = bundle.getString("subject");
		String message = bundle.getString("message");
		String attachment = bundle.getString("attachment");


		//make a new instance of MailSender and send it
		MailSender sender = new MailSender(username, password);
		Log.e("Birham : sendMail", "I am in run");
		try {
			sender.sendMail(subject, message, sendAddr, email, ccAddr,
					attachment);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*
	 * get one mail's content by folder and uid
	 */
	public String getOneMailContent(String folder, Integer uid)
			throws Exception {

		System.out.println("::getOneMailContent::");
		System.out.println("getOneMailContent is working");
		System.out.println("uid is" + uid);

		String content = "here is content";

		try {
			Store store = imapSession.getStore("imaps");
			// Connect to server by sending username and password.
			// Example mailServer = imap.gmail.com, username = abc, password =
			store.connect(username, password);

			IMAPFolder inbox = (IMAPFolder) store.getFolder(folder);
			if (!inbox.isOpen()) {
				inbox.open(Folder.READ_ONLY);
			}

			UIDFolder ufolder = (UIDFolder) inbox;
			Message message = ufolder.getMessageByUID(uid);
			
			//new instance of MailReciver 
			MailReciver pmm = new MailReciver((MimeMessage) message);
			
			//get the content
			pmm.getMailContent((Part) message);
			content = pmm.getBodyText();
			// content = (String) message.getContent();
			System.out.println("ContentType is" + message.getContentType());
			// System.out.println("ContentType is"+content);
		} catch (MessagingException e) {

			throw new RuntimeException(e);
		}
		// CommonApplication.debug("content is "+content);
		CommonApplication.dbOperation.updateMailContent(uid, content);
		return content;
	}
	/*
	 * get one mail content with folder name, uid and 
	 * boolean for whether to download content
	 */
	private MailModel getOneMail(String folderName, int uid, boolean withContent)
			throws Exception {
		TAG = "getOneMail" + TAG;
		
		String account = CommonApplication.ACCOUNT;

		String messageId = "";
		String fromWho = "";
		String toWho = "";
		String ccWho = "";
		String bccWho = "";
		String sendDate = "";
		String subject = "";
		String content = "";
		String attachment = "";
		String flags = "";

		try {
			MailModel mail = new MailModel();
			Store store = imapSession.getStore("imaps");
			// Connect to server by sending username and password.
			store.connect(username, password);

			IMAPFolder inbox = (IMAPFolder) store.getFolder(folderName);
			if (!inbox.isOpen()) {
				inbox.open(Folder.READ_ONLY);
			}
			//get the uid folder
			UIDFolder ufolder = (UIDFolder) inbox;
			
			//get the message
			Message message = ufolder.getMessageByUID((int) uid);
			
			MailReciver miao = new MailReciver((MimeMessage) message);

			messageId = miao.getMessageId();
			fromWho = miao.getFrom();
			SharedPreferencesUtil mSpUtil = CommonApplication.getInstance().getSpUtil();
			
			
			toWho = miao.getMailAddress("to");
			ccWho = miao.getMailAddress("cc");
			bccWho = miao.getMailAddress("bcc");

			sendDate = miao.getSentDate();
			subject = miao.getSubject();

//			if (withContent) {
				//download content
				miao.getMailContent((Part) message);
				content = miao.getBodyText();
//			}

			//read and inread
			attachment = "";
			Flags mflags = message.getFlags();
			if (mflags.contains(Flags.Flag.SEEN)) {
				L.i( TAG,"this mail has been read");
				flags = "read";
			} else {
				L.i( TAG,"this mail is unread");
				flags = "unread";
			}

			L.i( TAG," uid: " + uid);
			L.i( TAG," uid: " + uid);
			L.i( TAG," subject: " + miao.getSubject());
			L.i( TAG," Message-ID: " + miao.getMessageId());

			message.getMessageNumber();

			
			//save all data into a mail model
			mail.setMessageId(messageId);
			mail.setFromWho(fromWho);
			mail.setToWho(toWho);
			mail.setCcWho(ccWho);
			mail.setBccWho(bccWho);

			mail.setSendDate(sendDate);
			mail.setSubject(subject);
			mail.setContent(content);
			mail.setAttachment(attachment);
			mail.setFlags(flags);

			mail.setAccount(account);
			mail.setFolder(folderName);
			mail.setUid(uid);

			
			return mail;

		} catch (MessagingException e) {

			throw new RuntimeException(e);
		}
		// CommonApplication.debug("content is "+content);
	}

	
	/*
	 *  this method is invoke when the app run the first time
	 *  to get some basic data
	 */
	public boolean getLatestMails(String folderName, Integer num) {
		
		IMAPFolder imapFolder = openIMAPFolder(folderName, false);

		Message[] messages = null;
		try {
			messages = imapFolder.getMessages();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//get the latest 30 mail
		for (int i = messages.length - 1; i > messages.length - num; i--) {
			MailModel mail = new MailModel();

			try {
				int uid = (int) imapFolder.getUID(messages[i]);
				if(!CommonApplication.dbOperation.loadUIDSet("inbox").contains(uid)){
					mail = getOneMail(folderName,uid, false);
					saveInChatDatabase( mail);
					
					CommonApplication.dbOperation.save(mail);
				}
				
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		}
		return true;

	}
	
	
	public void saveInChatDatabase(MailModel mail){
		
	
		SharedPreferencesUtil mSpUtil = CommonApplication.getInstance().getSpUtil();
		UserDB mUserDb = CommonApplication.getInstance().getUserDB();
		MessageDB mMsgDb =CommonApplication.getInstance().getMessageDB();
		RecentDB mRecentDB = CommonApplication.getInstance().getRecentDB();
		
		
		TAG  = "saveInChatDatabase"+TAG;
		L.i(TAG, "");
		
		//将新用户存到地址
		String emailAddr = mail.getFromWho();
		String hisEmail = emailAddr.substring(emailAddr.indexOf("<")+1, emailAddr.lastIndexOf(">"));
		String hisNickName = emailAddr.substring(0,emailAddr.indexOf("<"));
		
		UserModel hisUserModel  =  mUserDb.selectInfo(hisEmail);
		if(hisUserModel==null&&!hisEmail.equals(mSpUtil.getEmail())){
			L.e(hisEmail);

			hisUserModel = new UserModel();
			hisUserModel.setEmail(hisEmail);
			hisUserModel.setNickName(hisNickName);
			if(hisNickName.equals("")){
				hisUserModel.setNickName(hisEmail);
			}
			mUserDb.addUser(hisUserModel);
		}
		
		MessageModel chatMessage = new MessageModel();
		chatMessage.setEmail(hisEmail);
		chatMessage.setCome(true);
		chatMessage.setMsgType(MessageModel.MESSAGE_TYPE_MAIL);
		
		if(mail.getFlags().indexOf("unread")>0){
			chatMessage.setNew(false);					
		}else{
			chatMessage.setNew(true);
		}
		
		chatMessage.setTime(TimeUtil.parseMailDateToChatMessageTime(mail.getSendDate()));

		chatMessage.setMessage(mGson.toJson(mail));
		L.i(TAG,mGson.toJson(chatMessage));
//		messageId = bundle.getString("messageId");
//		uid = bundle.getInt("uid");
//		
//		List<MailModel> mailist = CommonApplication.dbOperation.getMailByMessageIDFromLocal(messageId);
		mMsgDb.saveMsg(hisEmail, chatMessage);
		
		ConversationModel recentItem = new ConversationModel(hisEmail, hisUserModel.getAvatar(), hisUserModel.getNickName(),
				mGson.toJson(mail), 0, System.currentTimeMillis());
		
		CommonApplication.getInstance().getHandler().sendEmptyMessage(MainActivity.UPDATE_CHATS);
		
		
		L.i(TAG,recentItem.toString());

		mRecentDB.saveRecent(recentItem);
		
	}
	
}
