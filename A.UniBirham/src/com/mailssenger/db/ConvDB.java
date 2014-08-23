package com.mailssenger.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import android.content.Context;

import com.lidroid.xutils.exception.DbException;
import com.mailssenger.model.ConvModel;

/**
 * @date 20140823
 * @author Han
 *
 */
public class ConvDB extends BaseDB{
	


	public ConvDB(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	
	public LinkedList<ConvModel> getRecentList() {

		LinkedList<ConvModel> list = new LinkedList<ConvModel>();
		ArrayList<ConvModel> convList  = null;
		
		try {
			 convList =  (ArrayList<ConvModel>) db.findAll(ConvModel.class);
			
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(convList!=null&&convList.size()>0){
			for (int i = 0; i < convList.size(); i++) {
				list.add(convList.get(i));
			}
			
			Collections.sort(list);// 按时间降序
		}else{
			return new LinkedList<ConvModel>();
		}
		
		
		return list;
	}

	public void delRecent(String email) {
		try {
			db.deleteById(ConvModel.class, email);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isExist(String email) {
	
		ConvModel convModel = null;
		try {
			convModel = db.findById(ConvModel.class, email);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(convModel==null){
			return false;
		}else{
			return true;
		}
	}

}
