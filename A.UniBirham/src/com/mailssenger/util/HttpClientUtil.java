/**
 *  网络连接模块；
 * @author special
 * 2012-12-12下午10:42:35
 */

package com.mailssenger.util;

import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.mailssenger.CommonApplication;


public class HttpClientUtil{
	
	/*普通post*/
	public static String Post(String URL, List<NameValuePair> formparams) throws Exception {
		DefaultHttpClient client;
		
		try {
			 client = new DefaultHttpClient();
			//设置超时;
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			HttpConnectionParams.setSoTimeout(params, 60 * 1000);
			
			HttpResponse response;
			UrlEncodedFormEntity entity = null;
			if (formparams!=null) {
				entity = new UrlEncodedFormEntity(formparams, "utf-8");
			}
			HttpPost post = new HttpPost(URL);
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(entity);
			response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(), "utf-8").trim();
				//弱引用;
				WeakReference<String> resultReference=new WeakReference<String>(result);
				CommonApplication.debug(">>Net:>>"+resultReference.get());
				return resultReference.get();
			}
		} catch (Exception e) {
			return "networkerr";
		} finally {
			client=null;
		}
		return "networkerr";
	}
	
	/*带有验证的httppost*/
	public static String PostAuth(String URL, List<NameValuePair> formparams,String AuthUser,String AuthPass) throws Exception {
		
		DefaultHttpClient client;
		try {
			client = new DefaultHttpClient();
			
			//构造验证;
			java.net.URL _url=new java.net.URL(URL);
			client.getCredentialsProvider().setCredentials(
					new AuthScope(_url.getHost(),AuthScope.ANY_PORT, AuthScope.ANY_REALM),
					new UsernamePasswordCredentials(AuthUser,AuthPass));
			
			//设置超时;
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			HttpConnectionParams.setSoTimeout(params, 60 * 1000);
			
			HttpResponse response;
			UrlEncodedFormEntity entity = null;
			if (formparams!=null) {
				entity = new UrlEncodedFormEntity(formparams, "utf-8");
			}
			HttpPost post = new HttpPost(URL);
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(entity);
			response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(), "utf-8").trim();
				//弱引用;
				WeakReference<String> resultReference=new WeakReference<String>(result);
				CommonApplication.debug(">>Net:>>"+resultReference.get());
				return resultReference.get();
			}
		} catch (Exception e) {
			return "networkerr";
		} finally {
			client=null;
		}
		return "networkerr";
	}
	
	public static String postPic(String URL, MultipartEntity multipartEntity) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			
			//设置超时;
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			HttpConnectionParams.setSoTimeout(params, 60 * 1000);
			HttpResponse response;
			HttpPost post = new HttpPost(URL);
			post.setEntity(multipartEntity);
			response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(), "utf-8").trim();
				//弱引用;
				WeakReference<String> resultReference=new WeakReference<String>(result);
				CommonApplication.debug(">>Net:>>"+resultReference.get());
				return resultReference.get();
			}
		} catch (Exception e) {
			return "networkerr";
		} finally {
			client=null;
		}
		return "networkerr";
	}
	
}
