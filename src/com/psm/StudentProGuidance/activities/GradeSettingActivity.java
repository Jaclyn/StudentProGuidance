/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.entities.Academic;

import android.app.Activity;
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
 * - show the layout ("grade")
 * - manipulate grade setting
 *
 * Activity FROM
 * - MainMenuActivity
 * - ResultsActivity
 * 
 * Activity TO
 * - ResultsActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class GradeSettingActivity extends Activity {

	private EditText txtGradeA;
	private EditText txtGradeB;
	private EditText txtGradeC;
	private EditText txtGradeD;
	private Button btnSave;

	private AcademicDataSource acadatasource;

	private final String TAG = "GRADE SETTING ACTIVITY";
	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.grade);	

		txtGradeA = (EditText) findViewById(R.id.txtA_GS);
		txtGradeB = (EditText) findViewById(R.id.txtB_GS);
		txtGradeC = (EditText) findViewById(R.id.txtC_GS);
		txtGradeD = (EditText) findViewById(R.id.txtD_GS);
		btnSave = (Button) findViewById(R.id.btnSave_GS);

		btnSave.setOnClickListener(new ButtonOnClickListener());

		acadatasource = new AcademicDataSource(this);

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");

		Log.d(TAG,"academic_id "+academic_id);

		loadDataGrade();
	}


	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnSave_GS :

				handleBtnSaveClicked();
				break;

			}
		}
	}

	public void loadDataGrade(){

		try {
			acadatasource.open();
			Academic thisAcademic = acadatasource.getAcademicByID(academic_id);
			acadatasource.close();

			if(thisAcademic!=null){
				if(thisAcademic.getGrade_a()!=0){
					txtGradeA.setText(
							String.valueOf(thisAcademic.getGrade_a()));
				}
				if(thisAcademic.getGrade_b()!=0){
					txtGradeB.setText(
							String.valueOf(thisAcademic.getGrade_b()));
				}
				if(thisAcademic.getGrade_c()!=0){
					txtGradeC.setText(
							String.valueOf(thisAcademic.getGrade_c()));
				}
				if(thisAcademic.getGrade_d()!=0){
					txtGradeD.setText(
							String.valueOf(thisAcademic.getGrade_d()));
				}
			}

		} catch (Exception e) {
			Log.e(TAG, ""+e.getMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			acadatasource.close();
		}

	}

	/**
	 * This method handles the event when the 
	 * button submit results is clicked.
	 */
	public void handleBtnSaveClicked(){

		String a = txtGradeA.getText().toString();
		String b = txtGradeB.getText().toString();
		String c = txtGradeC.getText().toString();
		String d = txtGradeD.getText().toString();

		if(a.equals("")||b.equals("")
				||c.equals("")||d.equals("")){

			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();

		}
		else{

			float markA = Float.parseFloat(a);	
			float markB = Float.parseFloat(b);	
			float markC = Float.parseFloat(c);	
			float markD = Float.parseFloat(d);	

			if(markA<markB||markA<markC||markA<markD||
					markB<markC||markC<markD||markB<markD){
			Toast.makeText(this, "Invalid Mark Setting.Fail To Save.", 
						Toast.LENGTH_SHORT).show();
			}
			else{

				acadatasource.open();

				Academic academic = new Academic();
				academic.setGrade_a(markA);
				academic.setGrade_b(markB);
				academic.setGrade_c(markC);
				academic.setGrade_d(markD);
				academic.setId(academic_id);

				try {

					acadatasource.updateAcademicGrade(academic);
					acadatasource.close();

					Bundle bundle = new Bundle();
					bundle.putLong("academic_id", academic_id);
					Intent results = new Intent("android.intent.action.RESULTS");
					results.putExtras(bundle);
					startActivity(results);
					finish();

				} catch (Exception e) {

					Log.e(TAG+" - "+"handleBtnAddClicked ", ""+e.getMessage());
					Toast toast = Toast.makeText(this,  ""+e.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();
					acadatasource.close();
				}

			}

		}

	}	

}
