/**
 * This file contains the description of the LoginActivity class 
 * and its related activities.
 */
package com.psm.StudentProGuidance.activities;

import com.psm.StudentProGuidance.controllers.AcademicDataSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * This class handles
 * - using testing profile name="Testing User" & id = "1234"
 * - check if the session is available
 * - go to UserChooseSemActivity for existing user
 * - go to AcademicForAddActivity for new user
 * 
 * Activity FROM
 * - MainActivity
 * 
 * Activity TO
 * - UserChooseSemActivity
 * - AcademicForAddActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - userid
 * - name
 * 
 * @author Ng Xin Man
 */
public class SkipLoginActivity extends Activity{

	public static final String TAG = "SKIP LOGIN ACTIVITY";
	private Context mContext;
	private AcademicDataSource acadatasource; 

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * This method will decide whether to show this login page
	 * 	 or propagate to main menu
	 */
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext=this;
		
		acadatasource = new AcademicDataSource(this);

		Bundle bundleTo = new Bundle();
		String id = "1234";
		String name = "Testing User";
		bundleTo.putString("userid", id);
		bundleTo.putString("name", name);

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

	}

}