package com.mailssenger.api;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.util.Log;

import com.mailssenger.CommonApplication;
import com.mailssenger.util.HttpClientUtil;
import com.mailssenger.util.MultipartEntity;


public class UserAPI {
	/**
	 * 设置资料
	 * @param nickName
	 * @param realName
	 * @param sex
	 * @param signature
	 * @return String
	 */
	public String setUserInfo(String nickName, String realName,
			String sex, String signature) {
		
		String URL = CommonApplication.SERVER_URL + "set_info?token=" + CommonApplication.OAUTH_TOKEN
				+ "&nickname=" + nickName + "&realname=" + realName + "&sex="
				+ sex + "&signature=" + signature;
		
		String dataString = null;
		
		try {
			dataString = HttpClientUtil.Post(URL,
					new ArrayList<NameValuePair>());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		// 弱引用；
		WeakReference<String> dataStringReference = new WeakReference<String>(
				dataString);
		return dataStringReference.get();
	}

	/**
	 * 获取资料
	 * @param userName
	 * @return String
	 */
	public String getUserInfo(String userName) {
		String URL = CommonApplication.SERVER_URL + "get_info?token=" + CommonApplication.OAUTH_TOKEN
				+ "&username=" + userName;
		String dataString = null;
		try {
			dataString = HttpClientUtil.Post(URL,
					new ArrayList<NameValuePair>());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		CommonApplication.debug(dataString);
		// 弱引用；
		WeakReference<String> dataStringReference = new WeakReference<String>(
				dataString);
		return dataStringReference.get();
	}
	
	/**
	 * 上传头像
	 * 
	 * @param mContent
	 *  图片字节流 2012-12-13下午11:08:35
	 */

	public String uploadAvatar(byte[] mContent) {

		String URL = CommonApplication.SERVER_URL
				+ "set_avatar?token="
				+ CommonApplication.OAUTH_TOKEN ;

		// 使用Util里面的MultipartEntity的丰富参数
		// 为什么不用httpmine里面的MultipartEntity？貌似有BUG，只好用别人的了;
		MultipartEntity multipartEntity = new MultipartEntity();
		String dataString = null;
		try {

			//multipartEntity.addPart("token", CommonApplication.OAUTH_TOKEN);
			multipartEntity.addPart("file", 
					CommonApplication.getInstance().getSpUtil().getEmail(),
					new ByteArrayInputStream(mContent), true);
			
			Log.e("takon", CommonApplication.OAUTH_TOKEN);

			dataString = HttpClientUtil.postPic(URL, multipartEntity);

			dataString = HttpClientUtil.postPic(URL, multipartEntity);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		// 弱引用；
		WeakReference<String> dataStringReference = new WeakReference<String>(
				dataString);
		return dataStringReference.get();

	}
	

	/**
	 * 上传背景
	 * 
	 * @param mContent
	 *  图片字节流 2012-12-13下午11:08:35
	 */
	public String uploadBackground(byte[] mContent) {

		String URL = CommonApplication.SERVER_URL
				+ "set_background?token="
				+ CommonApplication.OAUTH_TOKEN ;

		// 使用Util里面的MultipartEntity的丰富参数
		// 为什么不用httpmine里面的MultipartEntity？貌似有BUG，只好用别人的了;
		MultipartEntity multipartEntity = new MultipartEntity();

		String dataString = null;
		try {
			multipartEntity.addPart("token", CommonApplication.OAUTH_TOKEN);
			multipartEntity.addPart("background", "filename",
					new ByteArrayInputStream(mContent), true);

			dataString = HttpClientUtil.postPic(URL, multipartEntity);
			dataString = HttpClientUtil.postPic(URL, multipartEntity);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		// 弱引用；
		WeakReference<String> dataStringReference = new WeakReference<String>(
				dataString);
		return dataStringReference.get();

	}
}
