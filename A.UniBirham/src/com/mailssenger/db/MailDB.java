/**
 * SQLDatabase;
 */
package com.mailssenger.db;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.tsz.afinal.FinalDb;
import android.content.Context;

import com.mailssenger.CommonApplication;
import com.mailssenger.model.MailModel;

public class MailDB {

	private FinalDb db;
	//construct;
	public MailDB(Context mContext) {
		super();
		this.db = FinalDb.create(mContext, CommonApplication.DB_NAME, true);
	}
	
	/*
	 * save one mail model
	 */
	public void save(MailModel mail){
		db.save(mail);
//		UIHelper.updateMainListAvtivityView();
	}
	
	/*
	 *mark mail as read in the local data base
	 */
	public void markMailAsSeenToLocal(int uid){
		MailModel mail=new MailModel();
		mail.setFlags("read");
		mail.setUid(uid);
		db.update(mail,"uid = "+uid +" AND folder ='"+"inbox"+"'");
//		UIHelper.updateMainListAvtivityView();
	}
	
	
	/*
	 *update mail with downloda content
	 */
	public void updateMailContent(int uid, String content){
		System.out.println("::updateMailContent::");
		MailModel mail=new MailModel();
		mail.setContent(content);
		mail.setUid(uid);
		db.update(mail,"uid = "+uid);
	}
	
	/*
	 *get mail form local database with messageId
	 */
	public List<MailModel> getMailByMessageIDFromLocal(String messageId){

		List<MailModel> mailist = db.findAllByWhere(MailModel.class,"messageId = '"+messageId+"'");

		return mailist;
	}
	
	

	/*
	 *load unread mail uid set form local database with floder name
	 */
	public Set<Integer> loadUIDSet(String folderName) {
		Set<Integer> uid_set = new HashSet<Integer>();
		List<MailModel> mailist = db.findAllByWhere(MailModel.class," folder ='"+folderName+"'");
		int len = mailist.size();
		if (len>0){
			for (int j = 0; j < len; j++) {
				uid_set.add(mailist.get(j).getUid());
			}
		}
		System.out.println("::loadUIDSet:: "+ folderName + uid_set);
		return uid_set;
	}
	
	
	/*
	 *load mail form local database with folder name
	 */
	public List<Map<String, Object>> loadMailData(String folderName) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		List<MailModel> mailist = db.findAllByWhere(MailModel.class,"folder = '"+ folderName +"' ORDER BY uid DESC");
		
		//limit 20
		//List<MailModel> mailist = db.findAll(MailModel.class,"uid DESC limit 20");

		//transform mail model in to list of MailModel
		Map<String, Object> map = null;

		int len = mailist.size();
		for (int j = 0; j < len; j++) {
			map = new HashMap<String, Object>();
			
			//construct a new map for mail
			map.put("messageId", mailist.get(j).getMessageId());
			
			map.put("fromWho", mailist.get(j).getFromWho());
			map.put("toWho", mailist.get(j).getToWho());
			map.put("ccWho", mailist.get(j).getCcWho());
			map.put("bccWho", mailist.get(j).getBccWho());
			
			map.put("sendDate", mailist.get(j).getSendDate());
			map.put("subject", mailist.get(j).getSubject());
			map.put("content", "" + mailist.get(j).getContent());
			map.put("attachment", "" + mailist.get(j).getAttachment());
			map.put("flags", "" + mailist.get(j).getFlags());
			
			map.put("account", "" + mailist.get(j).getAccount());
			map.put("folder", "" + mailist.get(j).getFolder());
			map.put("uid",  mailist.get(j).getUid());
			
			list.add(map);
		}
		return list;

	}

	
	/*
	 * save list of MailModel into local database
	 */
	public void saveMailData(List<MailModel> list, Context context) {
		
		Iterator<MailModel> it = list.iterator();
		while (it.hasNext()) {
			MailModel mailModel = (MailModel) it.next();
			db.save(mailModel);
		}

	}
}
