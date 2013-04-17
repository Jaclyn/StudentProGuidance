package com.psm.StudentProGuidance.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * This class is the entry point for the whole program.
 * It does 
 * - check the internet connection availability
 * - show the splash screen
 * - change to login/skiplogin screen
 * 
 * Activity FROM
 * - N/A
 * 
 * Activity TO
 * - SkipLoginActivity
 * - LoginActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */

public class MainActivity extends Activity {

	public static final String TAG = "STUDENT PRO GUIDANCE";
	private Intent login = new Intent("android.intent.action.LOGIN");
	private Intent skiplogin = new Intent("android.intent.action.SKIPLOGIN");
	private ProgressBar progress;
	private Handler handler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		setContentView(R.layout.main);

		progress = (ProgressBar) findViewById(R.id.progressBar_Main);

		/**
		 * This call method to check network connection status
		 * & propagate to LoginActivity which handle
		 * Facebook Login.
		 */
		//goFBLogin();
		/**
		 * This call method to skip network checking
		 * & propaget to SkipLoginActivity which
		 * start with user "Testing User" with 
		 * id = "1234"
		 */
		goSkipLogin();
	}


	/**
	 * Check internet connection availability
	 * @return true if in connection
	 * 			false if no connection
	 */
	public boolean isOnline(){
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 * Create dialog to notify no internet connection is found.
	 * 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case 0:
			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Notification")
			.setMessage("No Internet Connection found.")
			.setPositiveButton("OK", new
					DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton)
				{
					return;
				}
			})
			.create();	
		}
		return null;
	}

	/**
	 * This method will
	 * -check network connection
	 * -count down display as splash screen
	 * -propagate to LoginActivity
	 * -end this
	 */
	public void goFBLogin(){

		/** If connection is found */
		if(isOnline()==true){

			Log.d(TAG,"Online now");
			/** Count down for splash screen */
			new CountDownTimer(3000, 1000){

				@Override
				public void onFinish() {
					handler.post(new Runnable(){

						public void run() {
							/** End current activity 
							 * and show next login screen */

							/**FacebookLogin*/
							startActivity(login);
							finish();

							//finishActivity(0);
						}

					});
				}

				@Override
				public void onTick(long arg0) {
					// TODO Auto-generated method stub
				}

			}.start();
		}
		else{
			Log.d(TAG,"Disconnect now");
			/** show notification dialog
			 * state no connection is found */
			showDialog(0);
		}

	}

	/**
	 * This method will
	 * -count down display as splash screen
	 * -propagate to SkipLoginActivity
	 * -end this
	 */
	public void goSkipLogin(){

		// Do something long
		Runnable runnable = new Runnable() {
			public void run() {
				for (int i = 0; i <= 10; i++) {
					final int value = i;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.post(new Runnable() {
						public void run() {
							progress.setProgress(value);
							if(value==10){
								//**Without internet & Skip facebook Login*//*
								startActivity(skiplogin);
								finish();
							}
						}
					});
				}
			}
		};
		new Thread(runnable).start();

	}


}