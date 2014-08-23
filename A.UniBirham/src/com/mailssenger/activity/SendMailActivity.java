package com.mailssenger.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mailssenger.CommonApplication;
import com.mailssenger.R;
import com.mailssenger.mail.MailSender;

/**
 * http://www.javacodegeeks.com/2013/10/send-email-with-attachment-in-android.
 * html
 */
public class SendMailActivity extends BaseActivity implements OnClickListener {
	private SendMailActivity context = null;
	// onActivityResult request code
	private static final int REQUEST_CODE = 6384; 
	EditText editTextEmail, editTextSubject, editTextMessage, editTextccWho;
	Button btnSend, btnAttachment;
	String email, ccAddr, subject, message, attachmentFile;
	String username, password ,sendAddr;
	String attachment = "";
	Uri URI = null;
	
	private static final int PICK_FROM_GALLERY = 101;
	int columnIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.send_mail);
		
		//set up all the view
		editTextEmail = (EditText) findViewById(R.id.editTextTo);
		editTextccWho = (EditText) findViewById(R.id.editTextCcWho);
		editTextSubject = (EditText) findViewById(R.id.editTextSubject);
		editTextMessage = (EditText) findViewById(R.id.editTextMessage);
		btnAttachment = (Button) findViewById(R.id.buttonAttachment);
		btnSend = (Button) findViewById(R.id.buttonSend);

		btnSend.setOnClickListener(this);
		btnAttachment.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {

		if (v == btnAttachment) {
			
		}
		if (v == btnSend) {
			try {
				//if send button is click , 
				//get all the dat from actiity and send it
				username = CommonApplication.SACCOUNT;
				password = CommonApplication.PASSWORD;
				sendAddr = CommonApplication.ACCOUNT;

				email = editTextEmail.getText().toString();
				ccAddr = editTextccWho.getText().toString();

				subject = editTextSubject.getText().toString();
				message = editTextMessage.getText().toString();

				//see whether the URI is null ,if so 
				//set  the attachment to empty string
				if (URI != null) {
					System.out.println("URI is " + URI.toString());
					attachment = URI.toString();
				} else {
					System.out.println("URI is null");
				}
				
				//run the send mail thread
				new Thread(sendMailRunnable).start();
			

			} catch (Throwable t) {
				t.printStackTrace();
				Toast.makeText(this,
						"Request failed try again: " + t.toString(),
						Toast.LENGTH_LONG).show();
			}
		}

	}
	/*
	 * new thread for sending email
	 */
	Runnable sendMailRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			//try to send email
			MailSender sender = new MailSender(username, password);
			Log.e("Birham : sendMail", "I am in run");
			try {
				sender.sendMail(subject, message, sendAddr, email, ccAddr,
						attachment);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
//			Toast.makeText(context, "Mail sending thread is running!",
//					Toast.LENGTH_LONG).show();
		}
	};

}