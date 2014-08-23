package com.mailssenger.db;

import java.util.List;

import android.content.Context;

import com.mailssenger.model.UserModel;

/**
 * @date 20140823
 * @author Han
 *
 */
public class UserDB extends BaseDB {

	public UserDB(Context mContext) {
		super(mContext);

	}

	public UserModel getById(String email) {
		return super.findById(UserModel.class, email);
	}

	public List<UserModel> getAll() {
		return findAll(UserModel.class);
	}

	// public UserModel getLastUser() {
	// List<UserModel> userModels = null;
	// try {
	// userModels = db.findAll(UserModel.class);
	// } catch (DbException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return userModels.get(userModels.size());
	// }

}
