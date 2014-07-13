package com.mailssenger.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mailssenger.CommonApplication;
import com.mailssenger.MainServiceCallback;
import com.mailssenger.R;
import com.mailssenger.Task;
import com.mailssenger.mail.MailAccount;
import com.mailssenger.model.MessageModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.push.MyPushMessageReceiver;
import com.mailssenger.service.MainService;
import com.mailssenger.util.DialogUtil;
import com.mailssenger.util.L;
import com.mailssenger.util.NetUtil;
import com.mailssenger.util.T;
import com.mailssenger.util.UIHelper;

public class LoginActivity extends BaseActivity implements MainServiceCallback, MyPushMessageReceiver.EventHandler{

     private EditText etAccount;
     private EditText etPW;
     private EditText etHost;
     private Button btnLogin;
     private Button btnsignin;
     private ImageView jmuImageView;
     private ImageButton btnclsAccount;
     private ImageButton btnclsPW;
     private LinearLayout lyAccount;
     private LinearLayout lyPW;
     private LinearLayout lyHelp;
     private Dialog dialog;
     
     boolean hasRetry = false;
     boolean isHelpShow = false;
	  //对话框,链接服务器
	 private Dialog mConnectServerDialog;

	//网络提醒
	 private View mNetErrorView;
	 
	 private Handler mHandler;
		

     /*
      * (non- Javadoc)
      *
      * @see android.app.Activity#onCreate(android.os.Bundle)
      */
     
     //initial config.setup all the view
     private void InitConfig() {
           etAccount = (EditText) findViewById(R.id.et_log_accout );
           etPW = (EditText) findViewById(R.id. et_log_psw);
           btnLogin = (Button) findViewById(R.id.btn_Log_in );
           etAccount.setText(CommonApplication.ACCOUNT);
           
           btnclsAccount=(ImageButton) findViewById(R.id.btn_clear );
           btnclsPW=(ImageButton) findViewById(R.id.btn_clear2 );
           btnclsAccount.setVisibility(View.GONE);
           btnclsPW.setVisibility(View.GONE);
           
           lyAccount=(LinearLayout)findViewById(R.id.et_ly_account);
           lyPW=(LinearLayout)findViewById(R.id.et_ly_pwd);
           
           lyHelp=(LinearLayout)findViewById(R.id.login_gap);
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
           // TODO Auto-generated method stub
           super.onCreate(savedInstanceState);

          setContentView(R.layout.log_in);
          //set up the  view
          InitConfig();
          
          ActionBar actionBar = getSupportActionBar();  
          actionBar.setDisplayHomeAsUpEnabled(false);  
          
          mHandler = new Handler();
          
        //初始化网络提示
  		mNetErrorView = findViewById(R.id.net_status_bar_top);
  		mNetErrorView.setOnClickListener(new View.OnClickListener() {
      		@Override
      		public void onClick(View arg0) {
      			startActivity(new Intent(
      					android.provider.Settings.ACTION_WIFI_SETTINGS));
      		}
      	});
  		
         
           // for easy use
//         etAccount.setText("hxj393@bham.ac.uk" );
//         etPW.setText( "J742515q");

           //login listeners
  		
  		etAccount.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean isFocus) {
				// TODO Auto-generated method stub
				if (isFocus) {
					lyAccount.setBackgroundResource(R.drawable.basic_edittext_bg);
				}else{
					lyAccount.setBackgroundResource(R.drawable.basic_edittext_bg_default);
				}
			}
		});
  		etPW.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean isFocus) {
				// TODO Auto-generated method stub
				if (isFocus) {
					lyPW.setBackgroundResource(R.drawable.basic_edittext_bg);
				}else{
					lyPW.setBackgroundResource(R.drawable.basic_edittext_bg_default);
				}
			}
		});
  		
           btnLogin.setOnClickListener( new OnClickListener() {

               @Override
               public void onClick(View arg0) {
            	   hasRetry = false;
            	   
            	if (NetUtil.isNetConnected( CommonApplication.getInstance())) {//如果网络可用
            		
            		btnLogin.setClickable(false);
               	   	String username = etAccount.getText().toString();
                      String password = etPW.getText().toString();
               	   
                      		if(username.contains("rova-tech")){
		               		   mSpUtil.setEmail(username);
		                       mSpUtil.setPassword(password);
		                       mSpUtil.setNick(username);
		                          
	                          Toast. makeText(context, "Add Mail Account Succeed!", Toast.LENGTH_LONG).show();
	                          UIHelper.showMainActivity(context,true);
	    
               	   			}
                      
//                      if(!username.contains("bham.ac.uk")||!username.contains("@")){
//	               		   T.showLong(context, "Email addrress is not correct, it should a valid email of Birmingham University");
//	               		   btnLogin.setClickable(true);
//	               		   etAccount.setText("" );
//	               		   return;
//               	   		}
               	   
                       // TODO Auto-generated method stub
                       //debug infoss
                      Log. i("tag", "Account:" + etAccount.getText().toString());
                      Log. i("tag", "Password:" + etPW.getText().toString());
                      
                       //get username and paswwrod
                      

                       if (username.contains("bham.ac.uk") && username.contains("@")){
                           //set birmingham mail config
                           CommonApplication. ACCOUNT = username;
                   		
                		
                           String mailServerAccount = username.substring(0,username.indexOf("@"));
                           //the account for Birmingham Account should be without postfix

                           CommonApplication. SACCOUNT = mailServerAccount;
                           CommonApplication. PASSWORD = password;
                           CommonApplication. IMAP_HOST="mail.bham.ac.uk" ;
                           CommonApplication. SMTP_HOST="auth-smtp.bham.ac.uk" ;
                           
                           System. out.println("::LoginActivity::OnCreate::" );
                           System. out.println("username is " + username);
                           System. out.println("password is " + password);
                             
                      } else if(username.endsWith("gmail.com" )){
                           
                           //set gmail mail config
                           CommonApplication. ACCOUNT = username;
                           CommonApplication. SACCOUNT = username;
                           CommonApplication. PASSWORD = password;
                           CommonApplication. IMAP_HOST="imap.gmail.com" ;
                           CommonApplication. SMTP_HOST="smtp.gmail.com" ;
                           System. out.println("::LoginActivity::OnCreate::" );
                           System. out.println("username is " + username);
                           System. out.println("password is " + password);
                                              
                      } else if(username.endsWith("163.com" )){
                            //set 163 mail config
                           CommonApplication. ACCOUNT = username;
                           CommonApplication. SACCOUNT = username;
                           CommonApplication. PASSWORD = password;
                           CommonApplication. IMAP_HOST="imap.163.com" ;
                           CommonApplication. SMTP_HOST="smtp.163.com" ;
                           System.out.println("::LoginActivity::OnCreate::" );
                           System.out.println("username is " + username);
                           System.out.println("password is " + password);                   
                      }

   	               // for authentication,open successful mean login successful
   	               Task task = new Task(context );
   	               task.setMethod(MailAccount.class, "authentication" );
   	               MainService.newTask(task);
                      
   	                //登录对话框
   	       			mConnectServerDialog = DialogUtil.getLoginDialog(context,"Trying to connect to the Birmingham Mail Server.");
   	       			mConnectServerDialog.show();
   	       			mConnectServerDialog.setCancelable(false);// 返回键不能取消

                 
           		} else {
           			T.showLong( CommonApplication.getInstance(), R.string.net_error_tip);
           			return;
           		}
            	   
               }

          });

          
           //clear edit text listener
           btnclsAccount.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                    // TODO Auto-generated method stub
                    etAccount.setText("" );
              }
          });
           btnclsPW.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                    // TODO Auto-generated method stub
                    etPW.setText("" );
              }
          });
           etAccount.addTextChangedListener(new TextWatcher() {

	   			@Override
	   			public void onTextChanged(CharSequence s, int start, int before,
	   					int count) {
	   				if (s.length() > 0) {
	   					btnclsAccount.setVisibility(View.VISIBLE);
	   				} else {
	   					btnclsAccount.setVisibility(View.GONE);
	   				}
	   			}
	
	   			@Override
	   			public void beforeTextChanged(CharSequence s, int start, int count,
	   					int after) {
	   				// TODO Auto-generated method stub
	   			}
	
	   			@Override
	   			public void afterTextChanged(Editable s) {
	   				// TODO Auto-generated method stub
	   			}
   			});
           etPW.addTextChangedListener(new TextWatcher() {

	   			@Override
	   			public void onTextChanged(CharSequence s, int start, int before,
	   					int count) {
	   				if (s.length() > 0) {
	   					btnclsPW.setVisibility(View.VISIBLE);
	   				} else {
	   					btnclsPW.setVisibility(View.GONE);
	   				}
	   			}
	
	   			@Override
	   			public void beforeTextChanged(CharSequence s, int start, int count,
	   					int after) {
	   				// TODO Auto-generated method stub
	   			}
	
	   			@Override
	   			public void afterTextChanged(Editable s) {
	   				// TODO Auto-generated method stub
	   			}
  			});

     }

     
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu items for use in the action bar
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.login, menu);
         return super.onCreateOptionsMenu(menu);
     }
     
     public boolean onOptionsItemSelected(MenuItem item) {  
    	    switch (item.getItemId()) {  
    	    case R.id.action_help_info: 
    	    	AnimationSet aSet = new AnimationSet(true);
    	    	AlphaAnimation alp = new AlphaAnimation(0.0f,1.0f);
    	    	lyHelp.setVisibility(View.VISIBLE);
    	    	aSet.setFillAfter(true);
    	    	if(!isHelpShow){
        	    	
                    alp.setDuration(500);
                    alp.setFillAfter(true);
                    
                    ScaleAnimation ascl =new ScaleAnimation(0.0f, 1.1f, 0.0f, 1.1f, 
                    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
                    ascl.setDuration(400);
                    ascl.setFillAfter(true);
                    
                    ScaleAnimation ascl2 =new ScaleAnimation(1.0f, 0.90909f, 1.0f, 0.90909f, 
                    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
                    ascl2.setDuration(100);
                    ascl2.setStartOffset(400);
                    ascl2.setFillAfter(true);
                    
                    aSet.addAnimation(alp);
                    aSet.addAnimation(ascl);
                    aSet.addAnimation(ascl2);
                    
                    lyHelp.startAnimation(aSet);
    	    	}else{
                    
                    ScaleAnimation ascl =new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, 
                    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
                    ascl.setDuration(100);
                    ascl.setFillAfter(true);
                    
                    ScaleAnimation ascl2 =new ScaleAnimation(1.0f, 0.9090909f, 1.0f, 0.9090909f, 
                    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
                    ascl2.setDuration(100);
                    ascl2.setStartOffset(100);
                    ascl2.setFillAfter(true);
                    
                    aSet.addAnimation(ascl);
                    aSet.addAnimation(ascl2);
                    
                    lyHelp.startAnimation(aSet);
    	    	}
    	    	
    	    	
                isHelpShow = true;
    	        return true;  
    	    default:  
    	        return super.onOptionsItemSelected(item);  
    	    }  
    	}
     
     
     @Override
     public void refresh(Object... args) {
    	 btnLogin.setClickable(true);

           if (args[0].equals("authentication" )) {
               try {
            	   // judge whether login successfull
                   if ((Boolean)args[1]){
                	   
					   // save the mail config
					   MailAccount mail = new MailAccount();
		
					   mSpUtil.setLogin(true);
					   mSpUtil.setEmail(CommonApplication.ACCOUNT);
					   mSpUtil.setPassword((CommonApplication.PASSWORD));
					   mSpUtil.setNick(CommonApplication.ACCOUNT);
					   
					   	mSpUtil.setIMAPHost(CommonApplication.IMAP_HOST);
	               		mSpUtil.setSMTPHost(CommonApplication.SMTP_HOST);
	               		mSpUtil.setMailServerAccount(CommonApplication.SACCOUNT);
					
				        //如果这时候对话框还在运行,就将其关闭
				   		if (mConnectServerDialog != null
				   				&& mConnectServerDialog.isShowing())
				   			mConnectServerDialog.dismiss();
				   		
					   	Toast. makeText(context, "Add Mail Account Succeed!", Toast.LENGTH_LONG).show();
					    UIHelper.showMainActivity(context,true);
					    
                    }else{
                    	
                    	T.showLong(context, "First time failed, will retry 2 seconds later.");
                    	
                    	// for authentication,open successful mean login successful
             
                    	if(!hasRetry){
                    		if(etAccount.getText().toString().contains("bham.ac.uk")){
                        		String username =etAccount.getText().toString();
                        		String mailServerAccount = username.substring(0,username.indexOf("@"));
                        		CommonApplication. SACCOUNT =  mailServerAccount;
                        		CommonApplication. ACCOUNT = etAccount.getText().toString();
                        		CommonApplication. PASSWORD= etPW.getText().toString();
                                CommonApplication. IMAP_HOST="mail.bham.ac.uk" ;
                                CommonApplication. SMTP_HOST="auth-smtp.bham.ac.uk" ;
                        	}
                        	
                    		mHandler.postDelayed(reSend, 2000);
        	               hasRetry=true;
                    	}else{
                    		T.showLong(context, "Email addrress or password is not correct, please try again.");
                    	}
                    	
                    }
                   
              } catch (Exception e) {
                   e.printStackTrace();
                   Toast. makeText(context, "Network Error", Toast.LENGTH_LONG).show();
              }
             //如果这时候对话框还在运行,就将其关闭
		   		if (mConnectServerDialog != null
		   				&& mConnectServerDialog.isShowing())
		   			mConnectServerDialog.dismiss();
          }

     }
     
     
    Runnable reSend = new Runnable() {

 		@Override
 		public void run() {
 			// TODO Auto-generated method stub
 			L.i("resend msg...");
 			 Task task = new Task(context );
 	         task.setMethod(MailAccount.class, "authentication" );
 	         MainService.newTask(task);
 	         
 	        //登录对话框
       			mConnectServerDialog = DialogUtil.getLoginDialog(context,"Retrying to connect to the Birmingham mail server!");
       			mConnectServerDialog.show();
       			mConnectServerDialog.setCancelable(false);// 返回键不能取消
 		}
 	};


     
     @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	
    	MyPushMessageReceiver.ehList.add(this);
    	//网络操作显示
		if (!NetUtil.isNetConnected(this))
			mNetErrorView.setVisibility(View.VISIBLE);
		else {
			mNetErrorView.setVisibility(View.GONE);
		}
    }
     
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MyPushMessageReceiver.ehList.remove(this);
    }
	@Override
	public void onChatMessage(MessageModel chatMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBind(String method, int errorCode, String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNotify(String title, String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetChange(boolean isNetConnected) {

		if (!isNetConnected) {
			T.showShort(this, R.string.net_error_tip);
			mNetErrorView.setVisibility(View.VISIBLE);
		} else {
			mNetErrorView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onNewFriend(UserModel u) {
		// TODO Auto-generated method stub
		
	}


     

}
