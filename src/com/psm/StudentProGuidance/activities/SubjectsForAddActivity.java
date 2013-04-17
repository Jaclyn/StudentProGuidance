/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Subjects;

import android.app.Activity;
//import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class will
 * -brings the academic id from the SubjectsListActivity
 * -add new subjects details to database
 * 
 * Activity FROM
 * - SubjectsListActivity
 * 
 * Activity TO
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
public class SubjectsForAddActivity extends Activity {

	private EditText txtName;
	private EditText txtCode;
	private EditText txtAbbrev;
	private EditText txtCredit;
	private EditText txtChapter;
	private Button btnAdd;
	private SubjectsDataSource datasource;
	public static final String TAG = "SUBJECTS FOR ADD ACTIVITY";
	private long academic_id;
	//private Context context = this;

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
		Log.d(TAG,"academic_id"+academic_id);

	}

	/**
	 * This private class handles button OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {

			switch(view.getId()){
			case R.id.btnSubmit_S:
				handleButtonAddSubjects();
				break;
			}
		}

	}

	public void handleButtonAddSubjects(){

		Log.d(TAG+"-SubjectsActivity", "I'm handling Button Add");

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
		 create a new record*/

			Subjects subjects = new Subjects();
			subjects.setName(sub_name);
			subjects.setCode(sub_code);
			subjects.setAbbrev(sub_abbrev);
			subjects.setCredit(sub_credit);
			subjects.setTotalChapter(sub_chapter);
			subjects.setAcademic_id(academic_id);
			
			try {

				datasource.open();

				datasource.createSubjects(subjects);

				datasource.close();
				
				Intent subjectPage = new Intent("android.intent.action.SUBJECTSLIST");
				Bundle bundle = new Bundle();
				bundle.putLong("academic_id", academic_id);
				subjectPage.putExtras(bundle);
				startActivity(subjectPage);
				finish();
				

			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				datasource.close();

			}

		}
	}
	
}
