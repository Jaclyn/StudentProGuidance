/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.psm.StudentProGuidance.adapters.ExpandableListAdapter;
import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.AssignmentDataSource;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.controllers.ScheduleDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.controllers.TestDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Assignment;
import com.psm.StudentProGuidance.entities.Class;
import com.psm.StudentProGuidance.entities.Schedule;
import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.entities.Summary;
import com.psm.StudentProGuidance.entities.Test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author User
 *
 */
public class DayViewActivity extends Activity {

	private TextView lblDate;
	//private ImageView colorView;
	private ExpandableListView expandList;
	private long academic_id;
	private String today;
	private AcademicDataSource acadata;
	private ClassDataSource classdata;
	private SubjectsDataSource subdata;
	private AssignmentDataSource assdata;
	private ScheduleDataSource schedata;
	private TestDataSource testdata;
	private Summary summary;
	private int todayDay;
	private Calendar now;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dayview);

		lblDate = (TextView) findViewById(R.id.lblDate_DAY);
		//colorView = (ImageView) findViewById(R.id.color_DAY);
		expandList = (ExpandableListView) findViewById(R.id.expand_DAY);

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");
		today = from.getString("today");
		summary = new Summary();

		String[] splitToday = today.split("/");
		int day = Integer.parseInt(splitToday[0]);
		int month = Integer.parseInt(splitToday[1])-1;
		int year = Integer.parseInt(splitToday[2]);

		now = Calendar.getInstance();
		now.set(year, month, day);
		todayDay = now.get(Calendar.DAY_OF_WEEK);
		//todayDay = dayValue;
		/*switch(dayValue){
		case Calendar.SUNDAY:
			todayDay = "Sunday";
			break;
		case Calendar.MONDAY:
			todayDay = "Monday";
			break;
		case Calendar.TUESDAY:
			todayDay = "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			todayDay = "Wednesday";
			break;
		case Calendar.THURSDAY:
			todayDay = "Thursday";
			break;
		case Calendar.FRIDAY:
			todayDay = "Friday";
			break;
		case Calendar.SATURDAY:
			todayDay = "Saturday";
			break;
		}
		 */
		acadata = new AcademicDataSource(this);
		classdata = new ClassDataSource(this);
		assdata = new AssignmentDataSource(this);
		testdata = new TestDataSource(this);
		subdata = new SubjectsDataSource(this);
		schedata = new ScheduleDataSource(this);

		getRelatedDetails();

		String dateDisplay = (String) 
				android.text.format.DateFormat.format("yyyy-MM-dd(E)", now);
		lblDate.setText(dateDisplay);
		ExpandableListAdapter mAdapter = 
				new ExpandableListAdapter(this,summary);
		expandList.setAdapter(mAdapter);

		/*expandList.setOnGroupClickListener(new OnGroupClickListener() {

			public boolean onGroupClick(ExpandableListView arg0, View arg1,
					int groupPosition, long arg3) {
				return true;
			}
		});*/
	}


	public void getRelatedDetails(){

		try{

			Academic academic = new Academic();
			acadata.open();
			academic = acadata.getAcademicByID(academic_id);
			acadata.close();

			String[] semStart = academic.getStartdate().split("/");
			int startday = Integer.parseInt(semStart[0]);
			int startmonth = Integer.parseInt(semStart[1])-1;
			int  startyear = Integer.parseInt(semStart[2]);

			Calendar start = Calendar.getInstance();
			start.set( startyear,  startmonth,  startday);
			start.add(Calendar.DAY_OF_YEAR, -1);

			String[] semEnd = academic.getEnddate().split("/");
			int endday = Integer.parseInt(semEnd[0]);
			int endmonth = Integer.parseInt(semEnd[1])-1;
			int  endyear = Integer.parseInt(semEnd[2]);

			Calendar end = Calendar.getInstance();
			end.set( endyear,  endmonth,  endday);
			end.add(Calendar.DAY_OF_YEAR, 1);

			List<Class> classSet = new ArrayList<Class>();
			//If in semester duration
			if(now.after(start)&&now.before(end)){
				Class inClass = new Class();
				inClass.setAcademic_id(academic_id);
				inClass.setDay(todayDay);

				classdata.open();
				classSet = classdata.getAllRelatedClass(inClass);
				classdata.close();
			}

			Assignment inAssignment = new Assignment();
			inAssignment.setAcademic_id(academic_id);
			inAssignment.setDueDate(today);

			assdata.open();
			List<Assignment> assignSet = assdata.getAllRelatedAssignment(inAssignment);
			assdata.close();

			Test inTest = new Test();
			inTest.setAcademic_id(academic_id);
			inTest.setDate(today);

			testdata.open();
			List<Test> testSet = testdata.getAllRelatedTest(inTest);
			testdata.close();

			subdata.open();
			List<Subjects> subSet = subdata.getAllSubjects(academic_id);
			subdata.close();

			schedata.open();
			List<Schedule> scheSet = schedata.getAllSchedule(academic_id);
			schedata.close();

			summary.setAcademic_id(academic_id);
			summary.setAcademic(academic);
			summary.setAssignSet(assignSet);
			summary.setClassSet(classSet);
			summary.setScheSet(scheSet);
			summary.setSubSet(subSet);
			summary.setTestSet(testSet);

		}
		catch (Exception e){

			Log.e("Day View Activity", ""+e.getMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			acadata.close();
			classdata.close();
			assdata.close();
			testdata.close();
			subdata.close();
			schedata.close();

		}

	}
}
