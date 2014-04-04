package com.mailssenger.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mailssenger.CommonApplication;
import com.mailssenger.R;
import com.mailssenger.db.MessageDB;
import com.mailssenger.db.RecentDB;
import com.mailssenger.db.UserDB;
import com.mailssenger.model.MailModel;
import com.mailssenger.model.UserModel;
import com.mailssenger.util.KenBurnsView;
import com.mailssenger.util.SharedPreferencesUtil;

public class UserInfoActivity extends BaseActivity {

    private static final String TAG = "UserInfoActionBarActivity";

    private KenBurnsView mHeaderPicture;
    private ImageView mHeaderLogo;
    private View mHeader;

    private TextView mHeaderInfo; 
    
    //
    String hisEmail;
    UserModel hisUserModel ;
    
	//初始化工具
	private CommonApplication mApplication;
	private SharedPreferencesUtil mSpUtil;
	private UserDB mUserDB;
	private RecentDB mRecentDB;
	private MessageDB mMsgDB;
	private MediaPlayer mMediaPlayer;
	private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //初始化各工具
  		mApplication = CommonApplication.getInstance();
  		mSpUtil = mApplication.getSpUtil();
  		mGson = mApplication.getGson();
  		mUserDB = mApplication.getUserDB();
  		mMsgDB = mApplication.getMessageDB();
  		mRecentDB = mApplication.getRecentDB();
  		
		//bundle, get data from caller
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			hisEmail = bundle.getString("hisEmail");
			hisUserModel = mUserDB.getUser(hisEmail);
		}

        setContentView(R.layout.user_info);

        mHeader = findViewById(R.id.header);
        mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.picture0, R.drawable.picture1);
        mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
        
		mHeaderInfo = (TextView)findViewById(R.id.header_info);
		mHeaderInfo.setText(hisUserModel.getEmail());
		mHeaderLogo.setImageResource(CommonApplication.heads[hisEmail.charAt(1)%18]);//头像设置
//		mHeaderLogo.setImageDrawable(drawable)

    }

}
