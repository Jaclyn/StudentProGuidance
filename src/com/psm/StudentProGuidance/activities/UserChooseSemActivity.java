package com.psm.StudentProGuidance.activities;

import java.util.List;

import com.psm.StudentProGuidance.adapters.SemesterAdapter;
import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.entities.Academic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class handles
 * - show layout ("userchooselist")
 * - go to AcademicForAddActivity for new semester entry
 * - show up user name
 * - show the list of semester of the user login
 * - able to delete the selected semester on the list(through SemesterAdapter)
 * - enter the main menu of selected semester(through SemesterAdapter)
 * 
 * Activity FROM
 * - SkipLoginActivity
 * 
 * Activity TO
 * - AcademicForAddActivity
 * - MainMenuActivity (through SemesterAdapter)
 * 
 * Bundle IN (key)
 * - name
 * - userid
 * 
 * Bundle OUT (key)
 * - userid
 * - name
 * 
 * @author Ng Xin Man
 */
public class UserChooseSemActivity extends Activity {

	private TextView lblWelcome;
	private Button btnAdd;
	private ListView semList;

	private static final String TAG = "USER CHOOSE SEMESTER ACTIVITY";
	private String userId;
	private String name;
	private AcademicDataSource acadatasource;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userchooselist);

		lblWelcome = (TextView) findViewById(R.id.lblUser_UL);
		btnAdd = (Button) findViewById(R.id.btnAdd_UL);
		semList = (ListView) findViewById(R.id.list_UL);

		btnAdd.setOnClickListener(new ButtonOnClickListener());

		acadatasource = new AcademicDataSource(this);

		Bundle fromLogin = getIntent().getExtras();
		name = fromLogin.getString("name");
		userId = fromLogin.getString("userid");

		Log.d(TAG,"userID : "+userId);
		Log.d(TAG,"name : "+name);

		if(name.equals("") == false){
			lblWelcome.setText("Welcome Back! " + name);
		}

		if(userId.equals("") == false){
			setRowInList();
		}

	}

	/**
	 * This method handles
	 * - button onClick event of btnAdd
	 * - go to AcademicForAddActivity
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {

			Bundle bundleTo = new Bundle();
			bundleTo.putString("userid", userId );
			bundleTo.putString("name", name );
			Intent academic = new Intent("android.intent.action.ACADEMICADD");
			academic.putExtras(bundleTo);
			startActivity(academic);

		}

	}

	/**
	 * This method set all the semester records into listview
	 */
	public void setRowInList(){
		try {

			/**
			 * Select all the semester records
			 * and set it into the listview
			 */
			acadatasource.open();
			final List<Academic> academics
			= acadatasource.getAllAcademics(userId);
			acadatasource.close();

			if(academics.size()>0){

				SemesterAdapter semAdapter =
						new SemesterAdapter(this,academics);
				semList.setAdapter(semAdapter);
			}

		} catch (Exception e) {

			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			acadatasource.close();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setRowInList();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle("Exit");
		alert.setMessage("Confirm Exit?");
		alert.setButton("Yes", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) { 
				finish();
				return;
			} });   
		alert.setButton2("No", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
				return;  
			} 
		});  
		alert.show();
	}

}
