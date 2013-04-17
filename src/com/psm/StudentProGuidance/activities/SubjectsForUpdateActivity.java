/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Subjects;

import android.app.Activity;
import android.app.AlertDialog;
//import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class will
 * -brings the academic id & subjects_id
 * 	from the SubjectsListActivity
 * -handle update subjects details
 * 
 * Activity FROM
 * - SubjectsListActivity
 * 
 * Activity BACK
 * - SubjectsListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * - subjects_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class SubjectsForUpdateActivity extends Activity {

	private EditText txtName;
	private EditText txtCode;
	private EditText txtAbbrev;
	private EditText txtCredit;
	private EditText txtChapter;
	private Button btnAdd;
	private SubjectsDataSource datasource;
	public static final String TAG = "SUBJECTS UPDATE ACTIVITY";
	private long academic_id;
	private long subjects_id;
	//private Context context = this;
	private Subjects subjectsToUpdate;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subjects);

		Log.d(TAG, "IM in Subjects Activity");

		txtName = (EditText) findViewById(R.id.txtName_S);
		txtCode = (EditText) findViewById(R.id.txtCode_S);
		txtAbbrev = (EditText) findViewById(R.id.txtAbbrev_S);
		txtCredit = (EditText) findViewById(R.id.txtCredit_S);
		txtChapter = (EditText) findViewById(R.id.txtChapter_S);
		btnAdd = (Button) findViewById(R.id.btnSubmit_S);

		btnAdd.setOnClickListener(new ButtonOnClickListener());

		//access database
		datasource = new SubjectsDataSource(this);

		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");
		subjects_id = bundle.getLong("subjects_id");
		Log.d(TAG,"academic_id"+academic_id);
		Log.d(TAG,"subjects_id"+subjects_id);

		if(academic_id>0){

			datasource.open();

			//search if existing record regarding subjects id
			try {
				Subjects thisSubjects = new Subjects();

				thisSubjects = datasource.getCertainSubjects(subjects_id);
				datasource.close();

				if(thisSubjects!=null){

					txtName.setText(thisSubjects.getName());
					txtCode.setText(thisSubjects.getCode());
					txtAbbrev.setText(thisSubjects.getAbbrev());
					txtCredit.setText(String.valueOf(thisSubjects.getCredit()));
					txtChapter.setText(String.valueOf(thisSubjects.getTotalChapter()));
				}	

			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				datasource.close();
			}

		}
	}

	/**
	 * This private class handles button OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {

			switch(view.getId()){
			case R.id.btnSubmit_S:
				handleButtonUpdateSubjects();
				break;
			}
		}

	}

	/**
	 * This method will validate the input fields
	 * & call for update progress after confirmation.
	 */
	public void handleButtonUpdateSubjects(){

		Log.d(TAG+"-Subjects Update Activity", "I'm handling Button Update");

		String sub_name = (String) txtName.getText().toString();
		String sub_code = (String) txtCode.getText().toString();
		String sub_abbrev = (String) txtAbbrev.getText().toString();
		String sub_creditText = (String) txtCredit.getText().toString();
		String sub_chapterText = (String)  txtChapter.getText().toString();
		int sub_credit = 0;
		if(sub_creditText.equals("")==false){
			sub_credit = new Integer(sub_creditText);
		}
		int sub_chapter = 0;
		if(sub_chapterText.equals("")==false){
			sub_chapter = new Integer(sub_chapterText);
		}

		//validation of input fields
		if(sub_name.equals("")
				||sub_code.equals("")
				||sub_abbrev.equals("")
				||sub_credit==0
				||sub_chapter==0){

			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();

		}else{

			/**create new object of Subjects then 
			 pass it into the data source to 
			 update the record*/
			subjectsToUpdate = new Subjects();
			subjectsToUpdate.setName(sub_name);
			subjectsToUpdate.setCode(sub_code);
			subjectsToUpdate.setAbbrev(sub_abbrev);
			subjectsToUpdate.setCredit(sub_credit);
			subjectsToUpdate.setTotalChapter(sub_chapter);
			subjectsToUpdate.setId(subjects_id);
			subjectsToUpdate.setAcademic_id(academic_id);

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
	 * details of this subjects.
	 */
	public void updateProgress(){

		try {

			datasource.open();
			datasource.updateSubjects(subjectsToUpdate);			
			datasource.close();			
			Toast toast = Toast.makeText(this, "Successfully Update!", 
					Toast.LENGTH_SHORT);
			toast.show();

		} catch (Exception e) {

			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			datasource.close();

		}
	}

}
