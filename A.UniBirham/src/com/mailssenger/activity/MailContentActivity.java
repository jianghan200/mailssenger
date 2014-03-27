package com.mailssenger.activity;


import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.mailssenger.*;
import com.mailssenger.mail.MailAccount;
import com.mailssenger.model.*;
import com.mailssenger.service.MainService;
import com.mailssenger.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.mailssenger.R;

/**
 * @author Han
 * 
 */
public class MailContentActivity extends BaseActivity implements LogicObject {

	private MailContentActivity context = null;
	
	private Gson mGson;

	private TextView titleTextView = null;
	private TextView fromTextView = null;
	private TextView timeTextView = null;
	private ImageButton likeImageButton = null;
	private TextView contentTextView = null;
	
	
	private WebView contentWebView =null;

	String messageId = null;
	String subject = null;
	String time = null;
	String from = null;
	String content = null;
	String folder = null;
	int uid = 0;
	String flags = null;
	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_content);
		context = this;
		
		mGson = CommonApplication.getInstance().getGson();
		

		
		//bundel ,get all the data from the MailListActivity
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			
			MailModel mailModel = mGson.fromJson(bundle.getString("mail"), MailModel.class);
			messageId = mailModel.getMessageId();
			
			List<MailModel> mailist = CommonApplication.dbOperation.getMailByMessageIDFromLocal(messageId);
			
			if(mailist.size()>0){
				content = mailist.get(0).getContent();
				from = mailist.get(0).getFromWho();
				time = mailist.get(0).getSendDate();
				subject = mailist.get(0).getSubject();
				String toWho =mailist.get(0).getToWho();
				String ccWho = mailist.get(0).getCcWho();
				String bccWho = mailist.get(0).getBccWho();

				flags = mailist.get(0).getFlags();
				folder = mailist.get(0).getFolder();
				uid = mailist.get(0).getUid();
			}

//			System.out.println("::getIntentData::");
//			System.out.println("uid is " + uid);
//			System.out.println("content is " + content);
		} else {
			System.out.println("bundle is null");
		}
// set up all the view
		setupView();
		
	}

	@SuppressWarnings("deprecation")
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@SuppressWarnings("deprecation")
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * setuo view 
	 */
	private void setupView() {

		//initialize the view
		titleTextView = (TextView) context
				.findViewById(R.id.info_content_title);
		timeTextView = (TextView) context.findViewById(R.id.info_content_time);
		fromTextView = (TextView) context.findViewById(R.id.info_content_from);
		likeImageButton = (ImageButton) context
				.findViewById(R.id.info_content_like);
		contentTextView = (TextView) context
				.findViewById(R.id.info_content_main_text);
		contentWebView = (WebView) findViewById(R.id.info_content_web_view);   

		if (subject != null) {
			titleTextView.setText(subject);
		}
		if (time != null) {
			timeTextView.setText(""+time);
		}
		if (from != null) {
			fromTextView.setText(from);
		}
		
		showContent();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fm.app.logic.LogicObject#refresh(java.lang.Object[])
	 */
	@Override
	public void refresh(Object... args) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "I am in refresh", Toast.LENGTH_LONG).show();
		if (args[0].equals("getOneMailContent")) {
			try {
				//get content from server success
				//show it
				content = (String) args[1];				
				showContent();
				//mark the mail as read
				Task task = new Task(context,10);
				task.setMethod(MailAccount.class, "markMailAsSeenToServer", folder,uid);
				MainService.newTask(task);

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context,
						"Content can not be fetched!",
						Toast.LENGTH_SHORT).show();
			}
		}
		
		if(args[0].equals("markMailAsSeenToServer")){
			if ((String)args[1]=="yes"){
				Toast.makeText(this, "mail mark seen succeed!", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "mail mark seen failed!", Toast.LENGTH_LONG).show();
			}
		}
	}
	/*
	 * show the mail content
	 */
	public void showContent(){
		// if content is null then try to download it from server
		if (content == null ||content.equals("")  ) {
			contentTextView.setText("content is not download");
			
			Task task = new Task(context,10);
			task.setMethod(MailAccount.class, "getOneMailContent",folder,uid);
			MainService.newTask(task);
			
			Toast.makeText(context,
					"Trying to download content now ,please wait!",
					Toast.LENGTH_SHORT).show();
		}else {
			//content already download,show it
			
			//if content contain html show it with web view
			if(content.contains("</")){
				
			
				contentTextView.setVisibility(View.GONE);
				
				
				contentWebView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);   
				
				contentWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);   
				contentWebView.setHorizontalScrollBarEnabled(false);   
				contentWebView.getSettings().setSupportZoom(true);   
				contentWebView.getSettings().setBuiltInZoomControls(true);   
//				contentWebView.setInitialScale(70);   
				contentWebView.setHorizontalScrollbarOverlay(true);  
				DisplayMetrics dm = getResources().getDisplayMetrics();   
				   int scale = dm.densityDpi;   
				   if (scale == 240) { //    
					   contentWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);   
				    } else if (scale == 160) {   
				    	contentWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);   
				    } else {   
				    	contentWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);   
				}  
				
				contentWebView.setWebChromeClient(new WebChromeClient());  
				contentWebView.setVisibility(View.VISIBLE);
			}else{
				//show as plain text
				contentTextView.setText(content);
			}
			//mark the mail as read
			Task task = new Task(context,10);
			task.setMethod(MailAccount.class, "markMailAsSeenToServer", folder,uid);
			MainService.newTask(task);
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
        case android.R.id.home:
        	this.finish();
            break;
		}
		return super.onOptionsItemSelected(item);
		
	}

}
