package com.psm.StudentProGuidance.activities;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.psm.StudentProGuidance.controllers.LecturerDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Lecturer;
import com.psm.StudentProGuidance.entities.Subjects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * This class will 
 * - show layout ("lecturer")
 * - connect to database and update the record of lecturer
 * 
 * Activity FROM
 * - LecturerListActivity
 * 
 * Activity TO
 * - LecturerListActivity
 * - SubjectsListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class LecturerForUpdateActivity extends Activity{

	private TextView lblSubjects;
	private EditText name;
	private EditText mobile;
	private EditText office;
	private EditText email;
	private Spinner spinSubjects;
	private ImageButton btnAdd;
	private Button btnSubmit;
	public static final String TAG = "LECTURER UPDATE ACTIVITY";
	private SubjectsDataSource subdatasource;
	private LecturerDataSource lecdatasource;
	private Hashtable <String,String> hashSub 
	= new Hashtable<String,String>();
	private long academic_id;
	private long lecturer_id;
	private List<Subjects> listSubjects = new ArrayList<Subjects>();	
	private Context thisContext;
	private Lecturer lecturerToUpdate;

	/* (non-Javadoc)
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lecturer);
		thisContext = this;

		Log.d(TAG, "IM in Lecturer Activity");

		name = (EditText) findViewById(R.id.txtName_L);
		mobile = (EditText) findViewById(R.id.txtContactM_L);
		office = (EditText) findViewById(R.id.txtContactO_L);
		email = (EditText) findViewById(R.id.txtEmail_L);
		lblSubjects = (TextView) findViewById(R.id.lblSubjects2_L);
		spinSubjects = (Spinner) findViewById(R.id.spinSubjects_L);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd_L);
		btnSubmit = (Button) findViewById(R.id.btnSubmit_L);

		btnSubmit.setOnClickListener(new ButtonOnClickListener());
		btnAdd.setOnClickListener(new ButtonOnClickListener());

		lecdatasource = new LecturerDataSource(this);
		subdatasource = new SubjectsDataSource(this);

		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");
		lecturer_id = bundle.getLong("lecturer_id");

		Log.d(TAG, "academic_id : "+academic_id);
		setListIntoSpin();
		
		if(academic_id>0){
			LoadData();
		}

	}
	
	public void LoadData(){
		lecdatasource.open();

		//search if existing record regarding subjects id
		try {
			Lecturer thisLecturer = new Lecturer();

			thisLecturer = lecdatasource.getCertainLecturer(lecturer_id);
			lecdatasource.close();

			if(thisLecturer!=null){

				name.setText(thisLecturer.getName());
				mobile.setText(thisLecturer.getMobile());
				office.setText(thisLecturer.getOffice());
				email.setText(thisLecturer.getEmail());
				int index;
				int size = listSubjects.size();
				Log.d(TAG,"SIZE : "+size);
				
				for(index=0; index<listSubjects.size();index++){
					Log.d(TAG,"this= "+listSubjects.get(index).getId()
							+" subjects_id"+thisLecturer.getSubjects_id());
					if(listSubjects.get(index).getId()==
							thisLecturer.getSubjects_id()){
						Log.d(TAG,"MATCH");
						spinSubjects.setSelection(index);
						break;
					}
				}
			
			}	

		} catch (Exception e) {


			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();

			lecdatasource.close();
		}
	}

	/**
	 * This method set all the subjects record into
	 * spinnerSubjects
	 */
	public void setListIntoSpin(){
		
		Log.d(TAG, "SetListAgain!!!!");
		try{
			subdatasource.open();
			listSubjects = subdatasource.getAllSubjects(academic_id);
			subdatasource.close();

			if(listSubjects!=null){

				int size = listSubjects.size();
				List<String> itemspin = new ArrayList<String>();

				for(int index=0; index<size; index++){

					String key = listSubjects.get(index).getAbbrev();
					String code = listSubjects.get(index).getCode();
					String value = listSubjects.get(index).getName();
					itemspin.add(key);
					hashSub.put(key.toString(), code.toString()+"-"+value.toString());
				}

				final List<String> copyOfSpin = itemspin;
				if(copyOfSpin.size()>0){
					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,copyOfSpin);

					spinSubjects.setAdapter(adapter);
					Log.d(TAG,"setAdapter pass");

					spinSubjects.setOnItemSelectedListener(new OnItemSelectedListener(){

						public void onItemSelected(AdapterView<?> adapter,
								View view, int position, long id) {
							String show = hashSub.get(copyOfSpin.get(position).toString());
							lblSubjects.setText(show);
						}

						public void onNothingSelected(AdapterView<?> adapter) {
							// TODO Auto-generated method stub

						}

					});
				}
			}
		}catch(Exception e){
			Log.e(TAG+" - doInBackground ", ""+e.getMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			subdatasource.close();
		}

	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart");
		super.onRestart();
		setListIntoSpin();
		LoadData();
	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnAdd_L :
				Intent intent = new Intent("android.intent.action.SUBJECTSLIST");
				Bundle bundle = new Bundle();
				bundle.putLong("academic_id", academic_id);
				intent.putExtras(bundle);
				startActivity(intent);
				break;

			case R.id.btnSubmit_L :
				updateLecturer();
				break;
			}
		}
	}

	/**
	 * update lecturer record
	 */
	public void updateLecturer(){

		String lec_name = (String) name.getText().toString();
		String lec_mobile = (String) mobile.getText().toString();
		String lec_office = (String)office.getText().toString();
		String lec_email = (String) email.getText().toString();
		int selectedIndex = spinSubjects.getSelectedItemPosition();

		if(lec_name.equals("")
				||selectedIndex<0){
			
			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();

		}else{

			lecdatasource.open();

			lecturerToUpdate = new Lecturer();
			lecturerToUpdate.setName(lec_name);
			if(lec_mobile.equals("")==false){
				lecturerToUpdate.setMobile(lec_mobile);
			}
			if(lec_office.equals("")==false){
				lecturerToUpdate.setOffice(lec_office);
			}
			if(lec_email.equals("")==false){
				lecturerToUpdate.setEmail(lec_email);
			}
			lecturerToUpdate.setSubjects_id(
					listSubjects.get(selectedIndex).getId());
			
			lecturerToUpdate.setAcademic_id(academic_id);
			lecturerToUpdate.setId(lecturer_id);

			AlertDialog alert = new AlertDialog.Builder(this).create();
			alert.setTitle("Confirmation");
			alert.setMessage("Confirm Update?");
			alert.setButton("Yes", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) { 
					updateProgress();
					return;
				} });   
			alert.setButton2("No", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {  
					return;  
				} });  
			alert.show();
		}
	}
	
	/**
	 * This method will call out database for update the 
	 * details of this lecturer.
	 */
	public void updateProgress(){

			try {

				lecdatasource.open();
				lecdatasource.updateLecturer(lecturerToUpdate);			
				lecdatasource.close();			
			
				Toast toast = Toast.makeText(this, "Successfully Update!", 
						Toast.LENGTH_SHORT);
				toast.show();

			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				lecdatasource.close();

			}
	}

}
