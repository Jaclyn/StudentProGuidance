/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.ResultsDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Results;
import com.psm.StudentProGuidance.entities.Subjects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class will 
 * - show the layout ("results")
 * - connect to database and create new record of results
 *
 * Activity FROM
 * - MainMenuActivity
 * - GradeSettingActivity
 * 
 * Activity TO
 * - OverallActivity
 * - GradeSettingActivity
 * - ResultsListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class ResultsActivity extends Activity {


	private Spinner spinSubjects;
	private TextView lblAnalyse;
	private EditText txtTitle;
	private EditText txtPercentage;
	private EditText txtMarksSmall;
	private EditText txtMarksBig;
	private Button btnOverall;
	private Button btnGradeSetting;
	private Button btnSubmit;
	private Button btnView;
	private SubjectsDataSource subdatasource;
	private ResultsDataSource resultsdatasource;
	private AcademicDataSource acadatasource;

	private final String TAG = "RESULTS ACTIVITY";
	private long academic_id;
	private List<Subjects> listSubjects;
	private Hashtable <String,String> hashSub 
	= new Hashtable<String,String>();
	private Context thisContext;
	private float leftPercent;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);	
		thisContext = this;

		spinSubjects  = (Spinner) findViewById(R.id.spinSubjects_RE);
		lblAnalyse =  (TextView)  findViewById(R.id.lblAnalyse_RE);
		txtTitle = (EditText) findViewById(R.id.txtTitle_RE);
		txtPercentage = (EditText) findViewById(R.id.txtPercentage_RE);
		txtMarksSmall = (EditText) findViewById(R.id.txtMarksSmall_RE);
		txtMarksBig = (EditText) findViewById(R.id.txtMarksBig_RE);
		btnOverall = (Button) findViewById(R.id.btnOverall_RE);
		btnGradeSetting = (Button) findViewById(R.id.btnSetting_RE);
		btnSubmit= (Button) findViewById(R.id.btnSubmit_RE);
		btnView = (Button) findViewById(R.id.btnViewMark_RE);


		btnOverall.setOnClickListener(new ButtonOnClickListener());
		btnGradeSetting.setOnClickListener(new ButtonOnClickListener());
		btnSubmit.setOnClickListener(new ButtonOnClickListener());
		btnView.setOnClickListener(new ButtonOnClickListener());

		subdatasource = new SubjectsDataSource(this);
		resultsdatasource = new ResultsDataSource(this);
		acadatasource = new AcademicDataSource(this);

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");
		setListIntoSpin();
	}

	/**
	 * This method set all the subjects in database
	 * into spinner
	 */
	public void setListIntoSpin(){
		if(academic_id>0){

			try {
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

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,copyOfSpin);

					spinSubjects.setAdapter(adapter);
					Log.d(TAG,"setAdapter pass");

					spinSubjects.setOnItemSelectedListener(new OnItemSelectedListener(){

						public void onItemSelected(AdapterView<?> adapter,
								View view, int position, long id) {
							startAnalyse();
						}

						public void onNothingSelected(AdapterView<?> adapter) {
							// TODO Auto-generated method stub

						}

					});
				}

			} catch (Exception e) {

				Log.e(TAG, ""+e.getMessage());
				subdatasource.close();
			}
		}
	}
	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnOverall_RE :
				Intent intent = new Intent("android.intent.action.OVERALL");
				Bundle to = new Bundle();
				to.putLong("academic_id", academic_id);
				intent.putExtras(to);
				startActivity(intent);
				break;

			case R.id.btnSetting_RE :
				Intent intentGrade = new Intent("android.intent.action.GRADESETTING");
				Bundle bundleGrade = new Bundle();
				bundleGrade.putLong("academic_id", academic_id);
				intentGrade.putExtras(bundleGrade);
				startActivity(intentGrade);
				break;

			case R.id.btnSubmit_RE :
				handleBtnSubmitClicked();
				break;

			case  R.id.btnViewMark_RE :
				Intent intentView = new Intent("android.intent.action.RESULTSLIST");
				Bundle bundleView = new Bundle();
				bundleView.putLong("academic_id", academic_id);
				long subjects_id = listSubjects.get
						(spinSubjects.getSelectedItemPosition()).getId();
				bundleView.putLong("subjects_id",subjects_id);
				bundleView.putString("subjects_name", listSubjects.get
						(spinSubjects.getSelectedItemPosition()).getName());
				intentView.putExtras(bundleView);
				startActivity(intentView);
				break;

			}
		}
	}

	/**
	 * This method handles the event when the 
	 * button submit results is clicked.
	 */
	public void handleBtnSubmitClicked(){

		int selectedIndex = spinSubjects.getSelectedItemPosition();
		String title = txtTitle.getText().toString();
		String percentage = txtPercentage.getText().toString();
		String marks_big = txtMarksBig.getText().toString();
		String marks_small = txtMarksSmall.getText().toString();
		
		
		if(title.equals("")
				||selectedIndex<0
				||percentage.equals("")
				||marks_small.equals("")
				||marks_big.equals("")){

			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();

		}else{
			
			float marksS=0.0f,marksB=0.0f,percent=0.0f;
			if(percentage.equals("")==false){
				percent = Float.parseFloat(percentage);
			}
			if(marks_big.equals("")==false){
				marksB = Float.parseFloat(marks_big);
			}
			if(marks_small.equals("")==false){
				marksS = Float.parseFloat(marks_small);
			}

			
			if(leftPercent==0){


				Toast toast = Toast.makeText(this, "Task Complete!Invalid To Save.", 
						Toast.LENGTH_SHORT);
				toast.show();

			}else if(percent>100){

				Toast toast = Toast.makeText(this, "Please Verify The Percentage Distribution.", 
						Toast.LENGTH_SHORT);
				toast.show();

			}else if(percent>leftPercent){

				Toast toast = Toast.makeText(this, "Only "+leftPercent+"% is undefined.", 
						Toast.LENGTH_SHORT);
				toast.show();

			}else if(marksS>marksB){

				Toast toast = Toast.makeText(this, "Please Verify The Marks Input.", 
						Toast.LENGTH_SHORT);
				toast.show();

			}else{

				resultsdatasource.open();

				Results results = new Results();
				results.setTitle(title);
				results.setSubjects_id(listSubjects.get(selectedIndex).getId());
				results.setPercent(round(percent,2));
				results.setMarks_S(round(marksS,2));
				results.setMarks_B(round(marksB,2));
				float marks = marksS/marksB * percent;
				results.setMarks(round(marks,2));
				results.setAcademic_id(academic_id);

				try {

					resultsdatasource.createResults(results);
					resultsdatasource.close();

					clearInputFields();

					startAnalyse();

				} catch (Exception e) {

					Log.e(TAG, ""+e.getMessage());
					resultsdatasource.close();
				}

			}
		}

	}

	public void clearInputFields(){
		txtTitle.setText("");
		txtPercentage.setText("");
		txtMarksSmall.setText("");
		txtMarksBig.setText("");
	}

	public void startAnalyse(){		
		try{

			//Get all the grade pointer 
			acadatasource.open();
			Academic thisAcademic = acadatasource.getAcademicByID(academic_id);
			acadatasource.close();

			float grade_A=0;
			float grade_B=0;
			float grade_C=0;
			float grade_D=0;

			if(thisAcademic!= null){
				grade_A = thisAcademic.getGrade_a();
				grade_B = thisAcademic.getGrade_b();
				grade_C = thisAcademic.getGrade_c();
				grade_D = thisAcademic.getGrade_d();
			}

			List<Results> resultset = new ArrayList<Results>();
			Results inResults = new Results();
			inResults.setAcademic_id(academic_id);
			int selectedIndex = spinSubjects.getSelectedItemPosition();
			inResults.setSubjects_id(listSubjects.get
					(selectedIndex).getId());
			resultsdatasource.open();
			resultset = resultsdatasource.getAllResults(inResults);
			resultsdatasource.close();
			float totalMarks = 0.0f;
			float totalPercent = 0.0f;

			if(resultset != null){
				for(int index=0; index<resultset.size();index++){
					float marks = resultset.get(index).getMarks();
					float percent = resultset.get(index).getPercent();
					totalPercent+=percent;
					float total = marks;
					totalMarks +=total;
				}
			}

			totalMarks = round(totalMarks,2);

			float diffA = round(grade_A - totalMarks,2);
			float diffB = round(grade_B - totalMarks,2);
			float diffC = round(grade_C - totalMarks,2);
			float diffD = round(grade_D - totalMarks,2);
			leftPercent =  100.00f - totalPercent;
			String marksOwn = String.valueOf(totalMarks);

			StringBuilder analysis = new StringBuilder();

			String gradeOwn = new String();
			if(diffA<=0){
				gradeOwn="A";
			}else if(diffB<=0){
				gradeOwn ="B";
			}else if(diffC<=0){
				gradeOwn ="C";
			}else if(diffD<=0){
				gradeOwn = "D";
			}else{
				gradeOwn = "FAIL";
			}

			if(totalPercent==100.00f){

				analysis.append("THIS SUBJECTS TASKS COMPLETE!\n")
				.append("GRADE OWN : ").append(gradeOwn);
			}else{

				analysis
				.append("*********ANALYSING*********\n")
				.append("The MARKS own for now : ").append(marksOwn).append("\n")
				.append("The GRADE own for now : ").append(gradeOwn).append("\n")
				.append(String.valueOf(leftPercent)).append("% marks is UNDEFINED.\n");
				
				if(gradeOwn.equals("A")){
					analysis
					.append("Congratulation!");
				}else{
					
					analysis
					.append("MARKS to get for \n");

					if(diffA>0)
						analysis.append("A : ").append(String.valueOf(diffA)).append("\n");
					if(diffB>0)
						analysis.append("B : ").append(String.valueOf(diffB)).append("\n");
					if(diffC>0)
						analysis.append("C : ").append(String.valueOf(diffC)).append("\n");
					if(diffD>0)
						analysis.append("D : ").append(String.valueOf(diffD)).append("\n");
					
				}
						
			}
			lblAnalyse.setText(analysis.toString());

		}catch(Exception e){
			Log.e(TAG+"**SA", ""+e.getLocalizedMessage());
			acadatasource.close();
		}

	}


	/**
	 * Round to certain number of decimals
	 * 
	 * @param d
	 * @param decimalPlace
	 * @return
	 */
	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		startAnalyse();
	}



}
