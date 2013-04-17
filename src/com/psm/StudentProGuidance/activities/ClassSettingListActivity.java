/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.adapters.ClassSettingAdapter;
import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.controllers.ScheduleDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class will 
 * - show the layout ("class_settinglist")
 * - show the list of schedule regards the semester
 *
 * Activity FROM
 * - TimeTableActivity
 * 
 * Activity TO
 * - TimeTableActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
/**
 * @author User
 *
 */
public class ClassSettingListActivity extends Activity {

	private static final String TAG = "CLASS SETTING LIST ACTIVITY";
	private EditText txtCount;
	private ImageButton btnRefresh;
	private Button btnApply;
	private ListView list;
	private ClassDataSource cladatasource;
	private ScheduleDataSource shedatasource;
	private AcademicDataSource acadatasource;

	private long academic_id;
	private int maxClass;
	private List<Schedule> scheduleSet;
	private Context context;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_settinglist);
		context = this;

		txtCount = (EditText) findViewById(R.id.txtMaxClass_CSL);
		btnApply = (Button) findViewById(R.id.btnApply_CSL);
		btnRefresh = (ImageButton) findViewById(R.id.btnRefresh_CSL);
		list = (ListView) findViewById(R.id.list_CSL);

		btnApply.setOnClickListener(new ButtonOnClickListener());
		btnRefresh.setOnClickListener(new ButtonOnClickListener());

		//access database
		cladatasource = new ClassDataSource(this);
		shedatasource = new ScheduleDataSource(this);
		acadatasource = new AcademicDataSource(this);

		/***
		 * GET User ID From MainMenuActivity
		 */
		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");

		setRowInList();

	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {

			//validation of input fields
			if(txtCount.getText().toString().equals("")){

				Toast toast = Toast.makeText(context, "Please Verify Your Input Fields Before Proceed.", 
						Toast.LENGTH_SHORT);
				toast.show();

			}else{

				switch(view.getId()){
				case R.id.btnRefresh_CSL :

					refreshScheduleView();
					break;
				case R.id.btnApply_CSL :

					AlertDialog alert = new AlertDialog.Builder(context).create();
					alert.setTitle("Confirmation");
					alert.setMessage("Confirm Apply? All the related data will be affected.");
					alert.setButton("Yes", new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int which) { 
							updateNewScheduleSets();
							Intent intent = new Intent("android.intent.action.TIMETABLE");
							Bundle to = new Bundle();
							to.putLong("academic_id", academic_id);
							intent.putExtras(to);
							startActivity(intent);
							finish();
							return;
						} });   
					alert.setButton2("No", new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int which) {  
							return;  
						} });  
					alert.show();

					break;

				}
			}
		}
	}

	/**
	 * This method set the views for schedule from database or either
	 * default schedule set as 10 units max class per day
	 */
	public void setRowInList(){
		if(academic_id>0){

			//search if existing record regarding academic id
			try {

				acadatasource.open();
				maxClass = acadatasource.getAcademicByID(academic_id).getMaxClass();

				if(maxClass>0){

					shedatasource.open();
					scheduleSet = shedatasource.getAllSchedule(academic_id);
					txtCount.setText(String.valueOf(scheduleSet.size()));

				}else{
					maxClass = 10;

					scheduleSet = new ArrayList<Schedule>();

					int start = 7;
					int end = start + maxClass;
					int count = 1;

					txtCount.setText("10");

					shedatasource.open();
					do{

						StringBuilder sBegin =
								new StringBuilder()
						.append(pad(start))
						.append(":").append(pad(0));

						StringBuilder sEnd =
								new StringBuilder()
						.append(pad(start+1))
						.append(":").append(pad(0));

						Schedule newSchedule = new Schedule();
						newSchedule.setAcademic_id(academic_id);
						newSchedule.setBegin(sBegin.toString());
						newSchedule.setEnd(sEnd.toString());
						newSchedule.setIndex(count);

						Schedule thisSchedule = shedatasource.createSchedule(newSchedule);

						scheduleSet.add(thisSchedule);

						start++;
						count++;

					}while(start<end);

				}

				ClassSettingAdapter adapter = new ClassSettingAdapter(this,scheduleSet);
				list.setAdapter(adapter);

				acadatasource.close();
				shedatasource.close();

			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				acadatasource.close();
				shedatasource.close();
			}
		}
	}

	/**
	 * This method manipulates the view of time format
	 * @param c
	 * @return
	 */
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	/**
	 * This method refresh the schedule views while user click 
	 * button 'Refresh'
	 */
	public void refreshScheduleView(){
		try {
			String txtMaxClass = txtCount.getText().toString();
			maxClass = Integer.parseInt(txtMaxClass);
			if(maxClass<=0){
				Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
						Toast.LENGTH_SHORT);
				toast.show();

			}else{

				int start,end,count;
				start = 7 ;
				end = 7 + maxClass;
				count = 1;

				scheduleSet.clear();
				//Recreate schedules;
				do{

					StringBuilder sBegin =
							new StringBuilder()
					.append(pad(start))
					.append(":").append(pad(0));

					StringBuilder sEnd =
							new StringBuilder()
					.append(pad(start+1))
					.append(":").append(pad(0));

					Schedule newSchedule = new Schedule();
					newSchedule.setAcademic_id(academic_id);
					newSchedule.setBegin(sBegin.toString());
					newSchedule.setEnd(sEnd.toString());
					newSchedule.setIndex(count);

					scheduleSet.add(newSchedule);

					start++;
					count++;

				}while(start<end);

				ClassSettingAdapter adapter = new ClassSettingAdapter(this,scheduleSet);
				list.setAdapter(adapter);

			}
		} catch (Exception e) {
			Log.e(TAG, ""+e.getMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();

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

	/**
	 * This method retrieve new schedule set which is just set by the user
	 * and update the schedule records.
	 */
	public void updateNewScheduleSets(){

		List<Schedule> schedules = ClassSettingAdapter.schedules;
		int size = schedules.size();
		try {

			Academic thisAcademic = new Academic();
			thisAcademic.setMaxClass(maxClass);
			thisAcademic.setId(academic_id);

			acadatasource.open();
			acadatasource.updateAcademicMaxClass(thisAcademic);
			acadatasource.close();

			shedatasource.open();
			shedatasource.deleteSchedule(academic_id);

			for(int loop=0;loop<size;loop++){
				Log.d(TAG, "NEW SCHEDULE BEGIN:"+schedules.get(loop).getBegin()
						+" END:"+schedules.get(loop).getEnd());
				shedatasource.createSchedule(schedules.get(loop));
			}

			shedatasource.close();

			cladatasource.open();
			cladatasource.setScheduleZero(academic_id);
			cladatasource.close();

		} catch (Exception e) {
			Log.e(TAG, ""+e.getMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			shedatasource.close();
			acadatasource.close();
			cladatasource.close();
		}

	}


}
