/**
 * This files contains MainMenuActivity class and 
 * its implementation.
 */
package com.psm.StudentProGuidance.activities;

import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.ScheduleDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.others.SimpleGestureFilter;
//import com.psm.StudentProGuidance.others.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class will 
 * - show the user main menu layout ("mainmenu")
 * - show the selected semester
 * - a main entry point to get in each modules
 *
 * Activity FROM
 * - UserChooseSemActivity (through SemesterAdapter)
 * 
 * Activity TO
 * - AcademicForUpdateActivity
 * - SubjectsListActivity
 * - LecturerListActivity
 * - TimeTableActivity / ClassSettingActivity
 * - AssignmentListActivity
 * - TestListActivity
 * - RevisionActivity
 * - ResultsActivity / GradeSettingActivity
 * - CalendarViewActivity
 * 
 * Bundle IN (key)
 * - semester
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
/*public class MainMenuActivity extends Activity  implements
SimpleGestureListener {*/
public class MainMenuActivity extends Activity  {
	public static final String TAG = "MAIN MENU ACTIVITY";
	private TextView txtSemester;
	private ImageButton btnAcademic;
	private ImageButton btnSubjects;
	private ImageButton btnLecturer;
	private ImageButton btnClass;
	private ImageButton btnAssignment;
	private ImageButton btnTest;
	private ImageButton btnRevision;
	private ImageButton btnResults;
	private ImageButton btnCalendar;

	private AcademicDataSource acadatasource;
	private ScheduleDataSource schedatasource;

	private long academic_id;
	private int semester;
	//private SimpleGestureFilter gestureScanner;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * This method show the MainMenu UI.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		//gestureScanner = new SimpleGestureFilter(this,this);

		txtSemester = (TextView) findViewById(R.id.txtSemester_MM);
		btnAcademic = (ImageButton) findViewById(R.id.btnAcademic_MM);
		btnSubjects = (ImageButton) findViewById(R.id.btnSubjects_MM);
		btnLecturer = (ImageButton) findViewById(R.id.btnLecturer_MM);
		btnClass = (ImageButton) findViewById(R.id.btnClass_MM);
		btnAssignment = (ImageButton) findViewById(R.id.btnAssignment_MM);
		btnTest = (ImageButton) findViewById(R.id.btnTest_MM);
		btnRevision = (ImageButton) findViewById(R.id.btnRevision_MM);
		btnResults = (ImageButton) findViewById(R.id.btnResults_MM);
		btnCalendar = (ImageButton) findViewById(R.id.btnCalendar_MM);

		acadatasource = new AcademicDataSource(this);
		schedatasource = new ScheduleDataSource(this);

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");
		semester = from.getInt("semester");

		Log.d(TAG,"academic_id "+academic_id);

		String display = "Semester - " + String.valueOf (semester);
		txtSemester.setText(display);

		btnAcademic.setOnClickListener(new ButtonOnClickListener());
		btnSubjects.setOnClickListener(new ButtonOnClickListener());
		btnLecturer.setOnClickListener(new ButtonOnClickListener());
		btnClass.setOnClickListener(new ButtonOnClickListener());
		btnAssignment.setOnClickListener(new ButtonOnClickListener());
		btnTest.setOnClickListener(new ButtonOnClickListener());
		btnRevision.setOnClickListener(new ButtonOnClickListener());
		btnResults.setOnClickListener(new ButtonOnClickListener());
		btnCalendar.setOnClickListener(new ButtonOnClickListener());
	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {

			Bundle bundle = new Bundle();
			bundle.putLong("academic_id", academic_id);
			bundle.putLong("assignment_id", 0);

			switch(view.getId()){

			case R.id.btnAcademic_MM :
				Intent academic = new Intent("android.intent.action.ACADEMICUPDATE");
				academic.putExtras(bundle);
				startActivity(academic);

				break;

			case R.id.btnSubjects_MM :
				Intent subjects = new Intent("android.intent.action.SUBJECTSLIST");
				subjects.putExtras(bundle);
				startActivity(subjects);

				break;

			case R.id.btnLecturer_MM :
				Intent lecturer = new Intent("android.intent.action.LECTURERLIST");
				lecturer.putExtras(bundle);
				startActivity(lecturer);

				break;

			case R.id.btnClass_MM :
				
				checkClassSetting(bundle);
				
				break;

			case R.id.btnAssignment_MM :
				Intent assignment = new Intent("android.intent.action.ASSIGNMENTLIST");
				assignment.putExtras(bundle);
				startActivity(assignment);

				break;

			case R.id.btnTest_MM :
				Intent test = new Intent("android.intent.action.TESTLIST");
				test.putExtras(bundle);
				startActivity(test);

				break;

			case R.id.btnRevision_MM :
				Intent revision = new Intent("android.intent.action.REVISION");
				revision.putExtras(bundle);
				startActivity(revision);

				break;

			case R.id.btnResults_MM :
				checkGradeInput(bundle);

				break;

			case R.id.btnCalendar_MM :
				Intent calendar = new Intent("android.intent.action.CALENDAR");
				calendar.putExtras(bundle);
				startActivity(calendar);

				break;

			}
		}
	}
	
	/**
	 * checkClassSetting
	 * - check if the class schedule is already set or not,
	 *   if set, go to time table page,
	 *   otherwise, go to class setting page.
	 * @param none
	 * @return void
	 */
	public void checkClassSetting(Bundle bundle){
		
		try {
			schedatasource.open();
			int found = schedatasource.findSchedule(academic_id);
			schedatasource.close();
			
			if(found>0){
				Intent timetable = new Intent("android.intent.action.TIMETABLE");
				timetable.putExtras(bundle);
				startActivity(timetable);
			}else{

				Intent clssSetting = new Intent("android.intent.action.CLASSSETTING");;
				clssSetting.putExtras(bundle);
				startActivity(clssSetting);
			}
				
		} catch (Exception e) {
			Log.e(TAG,""+ e.getLocalizedMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			schedatasource.close();
		}
	}

	/**
	 * checkGradeInput
	 * - check if the grade is already set or not,
	 *   if set, go to results page,
	 *   otherwise, go to grade setting page.
	 * @param none
	 * @return void
	 */
	public void checkGradeInput(Bundle bundle){

		try {
			acadatasource.open();
			Academic thisAcademic = acadatasource.getAcademicByID(academic_id);
			acadatasource.close();
			
			float gradeA = 0;
			float gradeB = 0;
			float gradeC = 0;
			float gradeD = 0;
			
			gradeA = thisAcademic.getGrade_a();
			gradeB = thisAcademic.getGrade_b();
			gradeC = thisAcademic.getGrade_c();
			gradeD = thisAcademic.getGrade_d();
			
			if(gradeA==0||gradeB==0||gradeC==0||gradeD==0){
				Intent gradeSet = new Intent("android.intent.action.GRADESETTING");
				gradeSet.putExtras(bundle);
				startActivity(gradeSet);
			}else{
				Intent results = new Intent("android.intent.action.RESULTS");
				results.putExtras(bundle);
				startActivity(results);
			}
		} catch (Exception e) {
			Log.e(TAG, ""+e.getLocalizedMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			acadatasource.close();
		}

	}
	
	
	/*	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		//this.gestureScanner.onTouchEvent(me);
		//return super.dispatchTouchEvent(me); 
		//return this.gestureScanner.onTouchEvent(me);
	}*/


	public void onSwipe(int direction) {

		switch (direction) {

		case SimpleGestureFilter.SWIPE_RIGHT : 
			//str = "Swipe Right";
			//viewFlipper.showNext();

			break;
		case SimpleGestureFilter.SWIPE_LEFT :  
			//str = "Swipe Left";
			Intent calendar = new Intent("android.intent.action.CALENDAR");
			Bundle bundle = new Bundle();
			bundle.putLong("academic_id", academic_id);
			bundle.putInt("semester", semester);
			calendar.putExtras(bundle);
			startActivity(calendar);
			finish();
			break;
		case SimpleGestureFilter.SWIPE_DOWN :  
			//	str = "Swipe Down";
			break;
		case SimpleGestureFilter.SWIPE_UP :    
			//	str = "Swipe Up";
			break;

		} 
		//Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void onDoubleTap() {
		// TODO Auto-generated method stub

	}

}
