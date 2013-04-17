package com.psm.StudentProGuidance.activities;
/**
 * This file contains the description of the LoginActivity class 
 * and its related activities.
 *//*
package com.psm.StudentProGuidance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

*//**
 * This class handles
 * - set up the connection to Facebook apps.
 * - check if the session is available
 * - decide whether to skip this login page or not
 * @author Ng Xin Man
 *//*
public class LoginActivity extends Activity{

	private static final String APP_ID = "321144831291546";
	private static final String[] PERMS = new String[] { "read_stream" };
	public static final String TAG = "LOGIN ACTIVITY";
	public Facebook mFacebook;
	private Activity mActivity = this;
	private Context mContext;
	private SharedPreferences sharedPrefs;
	private ImageButton btnLoginFB;
	private AsyncFacebookRunner mAsyncRunner;
	private AcademicDataSource acadatasource; 

	 (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * This method will decide whether to show this login page
	 * 	 or propagate to main menu
	 
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//Set up Facebook connection 
		setConnection();
		acadatasource = new AcademicDataSource(this);

		//Check if the session exists
		if(isSession()){

			setContentView(R.layout.main);
			//Get data then Go to main menu
			mAsyncRunner = new AsyncFacebookRunner(this.mFacebook);
			mAsyncRunner.request("me", new IDRequestListener());

		}else{

			//Set up the UI of login page
			setContentView(R.layout.login);
			//Access the related components of UI page
			btnLoginFB = (ImageButton) findViewById(R.id.btnLoginFB);

			//Assign the tasks to the related components
			btnLoginFB.setOnClickListener(new ButtonOnClickListener());
		}
	}

	*//**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 *//*
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View arg0) {
			//Show facebook login page
			mFacebook.authorize(mActivity, PERMS,new LoginDialogListener());
		}
	}

	*//**
	 * Set up Facebook connection
	 *//*
	public void setConnection() {
		mContext = this;
		mFacebook = new Facebook(APP_ID);
		Log.d(TAG, "Connected");
	}

	*//**
	 * Check if the Facebook session is valid or not
	 * @return true - if session valid
	 * 			false - if invalid
	 *//*
	public boolean isSession() {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		String access_token = sharedPrefs.getString("access_token", "x");
		Long expires = sharedPrefs.getLong("access_expires", -1);
		Log.d(TAG, access_token);

		if (access_token != null && expires != -1) {
			mFacebook.setAccessToken(access_token);
			mFacebook.setAccessExpires(expires);
		}
		return mFacebook.isSessionValid();
	}	

	*//**
	 * THis class handles the event when user starts 
	 * the Facebook login page dialog
	 * @author Ng Xin Man
	 *//*
	private class LoginDialogListener implements DialogListener {

		public void onComplete(Bundle values) {
			Log.d(TAG, "LoginONComplete");
			String token = mFacebook.getAccessToken();
			long token_expires = mFacebook.getAccessExpires();
			Log.d(TAG, "AccessToken: " + token);
			Log.d(TAG, "AccessExpires: " + token_expires);
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			sharedPrefs.edit().putLong("access_expires", token_expires)
			.commit();
			sharedPrefs.edit().putString("access_token", token).commit();

			mAsyncRunner = new AsyncFacebookRunner(mFacebook);
			mAsyncRunner.request("me", new IDRequestListener());
		}

		public void onCancel() {
			Log.d(TAG, "OnCancel");
		}

		public void onFacebookError(FacebookError e) {
			Log.d(TAG, "FacebookError: " + e.getMessage());

		}

		public void onError(DialogError e) {
			Log.d(TAG, "Error: " + e.getMessage());

		}
	}

	*//**
	 * This private class handles the Facebook ID request 
	 * and display the user name.
	 * @author Ng Xin Man
	 *//*
	private class IDRequestListener implements RequestListener {

		public void onComplete(String response, Object state) {
			try {
				Log.d(TAG, "IDRequestONComplete");
				Log.d(TAG, "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				Log.d(TAG, "Json: " + json.toString());
				final String name = json.getString("name");
				final String id = json.getString("id");

				Log.d("Json",name);

				Bundle bundleTo = new Bundle();
				bundleTo.putString("userid", id);
				bundleTo.putString("name", name);

				Log.d("Login","name : "+name);
				Log.d("Login","bundle name : "+bundleTo.getString("name"));

				acadatasource.open();

				try {
					//User have record
					if(acadatasource.getAcademicByUserID(id)!=null){
						
						acadatasource.close();
						Intent userSem = new Intent("android.intent.action.USERSEM");
						userSem.putExtras(bundleTo);
						startActivity(userSem);
						finish();
					}
					//User don't have record
					else{
						
						acadatasource.close();
						Intent academic = new Intent("android.intent.action.ACADEMICADD");
						academic.putExtras(bundleTo);
						startActivity(academic);
						finish();
					}
				} catch (Exception e) {
					
					Log.e(TAG, ""+e.getMessage());
					AlertDialog alert = new AlertDialog.Builder(mContext).create();
					alert.setTitle("Error");
					alert.setMessage(""+e.getMessage());
					alert.setButton("OK", new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int which) {  
							return;  
						} });   
					alert.show();
					acadatasource.close();
					
				}


			} catch (JSONException e) {
				Log.d(TAG, "JSONException: " + e.getMessage());
			} catch (FacebookError e) {
				Log.d(TAG, "FacebookError: " + e.getMessage());
			}
		}

		public void onIOException(IOException e, Object state) {
			Log.d(TAG, "IOException: " + e.getMessage());
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			Log.d(TAG, "FileNotFoundException: " + e.getMessage());
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			Log.d(TAG, "MalformedURLException: " + e.getMessage());
		}

		public void onFacebookError(FacebookError e, Object state) {
			Log.d(TAG, "FacebookError: " + e.getMessage());
		}

	}

	 (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 * This method handles the activity which sends response back to this activity class.
	 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}


}

*/