/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.controllers.ScheduleDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Class;
import com.psm.StudentProGuidance.entities.Schedule;
import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.others.AlarmService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This class will 
 * - show layout ("class_arrangement")
 * - connect to database and create new record of class
 * 
 * Activity FROM
 * - ClassListActivity
 * 
 * Activity TO
 * - SubjectsListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class ClassForAddActivity extends Activity {

	private Spinner spinSubjects;
	private TextView lblSubjects;
	private TextView lblRemindTime;
	private EditText txtTitle;
	private Spinner spinWeekday;
	private ToggleButton btnReminder;
	private Spinner spinTimeFrom;
	private Spinner spinTimeTo;
	private Button btnSubmit;
	private List<String> weekday;
	private ClassDataSource cladatasource;
	private ScheduleDataSource schdatasource;
	private SubjectsDataSource subdatasource;
	private AcademicDataSource acadatasource;
	private long academic_id;
	private ImageButton btnAdd;
	static final int TIME_DIALOG_ID = 0;
	private List<Schedule> scheList;
	private List<Subjects> listSubjects;
	private Hashtable <String,String> hashSub 
	= new Hashtable<String,String>();
	private Hashtable <Integer,Integer> hashDay 
	= new Hashtable<Integer,Integer>();
	private final String TAG = "CLASS FOR ADD ACTIVITY";
	private Context thisContext;
	private int mHour;
	private int mMinutes;
	private final int TIME_DIALOG = 1;
	private final int REMINDER = 0;
	private int BUTTON_ACCESS = 0;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private long createdId;
	private ArrayAdapter<String> weekdayAdapter;
	private List<Integer> daySet;
	private static int AlarmStartCategory = 1000;
	private int thisAlarmID;
	private int selectedDay;
	private String description;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_arrangement);	
		thisContext = this;

		spinSubjects = (Spinner) findViewById(R.id.spinSubjects_CLA);
		lblSubjects = (TextView) findViewById(R.id.lblSubjects2_CLA);
		txtTitle  = (EditText) findViewById(R.id.txtTitle_CLA);
		spinWeekday = (Spinner) findViewById(R.id.spinDay_CLA);
		lblRemindTime = (TextView) findViewById(R.id.lblReminderChoice_CLA);
		btnAdd = (ImageButton) findViewById(R.id.btnAdd_CLA);
		btnReminder = (ToggleButton) findViewById(R.id.btnReminder_CLA);
		spinTimeFrom = (Spinner) findViewById(R.id.spinTimeFrom_CLA);
		spinTimeTo = (Spinner) findViewById(R.id.spinTimeTo_CLA);
		btnSubmit = (Button) findViewById(R.id.btnSubmit_CLA);

		btnAdd.setOnClickListener(new ButtonOnClickListener());
		btnSubmit.setOnClickListener(new ButtonOnClickListener());
		btnReminder.setOnClickListener(new ButtonOnClickListener());

		/*	List<Schedule> scheList = new ArrayList<Schedule>();
		List<Subjects> listSubjects = new ArrayList<Subjects>();*/
		weekday = new ArrayList<String>();

		//Get current System date
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinutes = c.get(Calendar.MINUTE);

		//access database
		acadatasource = new AcademicDataSource(this);
		cladatasource = new ClassDataSource(this);
		schdatasource = new ScheduleDataSource(this);
		subdatasource = new SubjectsDataSource(this);

		/*	schdatasource.open();
		scheList = schdatasource.getAllSchedule(academic_id);
		schdatasource.close();*/

		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");

		if(academic_id>0){


			try {
				acadatasource.open();
				Academic thisAcademic = acadatasource.getAcademicByID(academic_id);
				acadatasource.close();

				String[] splitDays = thisAcademic.getDaySet().split(",");
				daySet = new ArrayList<Integer>();
				for(int i=0;i<splitDays.length;i++){
					daySet.add(Integer.parseInt(splitDays[i]));
				}
				Log.d(TAG, "DAYSET:"+daySet.size());
				//weekday = new String[]();
				int index=0;
				for(int j=Calendar.SUNDAY;j<=Calendar.SATURDAY;j++){

					if(daySet.contains(j)){
						String day = new String();
						switch(j){
						case 1:day="Sunday";
						break;
						case 2:day="Monday";
						break;
						case 3:day="Tuesday";
						break;
						case 4:day="Wednesday";
						break;
						case 5:day="Thursday";
						break;
						case 6:day="Friday";
						break;
						case 7:day="Saturday";
						break;
						}
						weekday.add(day);
						hashDay.put(index, j);
						//Log.d(TAG, "WEEKDAY:"+weekday.get(index));
						//Log.d(TAG, "HASHDAY INDEX:"+index+" DAY:"+j);
						index++;
					}
				}

				Log.d(TAG, "WEEKDAY SIZE:"+weekday.size());

				weekdayAdapter = new ArrayAdapter<String>
				(this,android.R.layout.simple_spinner_item,weekday);

				spinWeekday.setAdapter(weekdayAdapter);

				setSubjectsListIntoSpin();
				setScheduleListIntoSpin();

			} catch (Exception e) {
				Log.e(TAG+" - doInBackground ", ""+e.getMessage());
				acadatasource.close();
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
			case R.id.btnAdd_CLA:
				Intent intent = new Intent("android.intent.action.SUBJECTSLIST");
				Bundle to = new Bundle();
				to.putLong("academic_id", academic_id);
				intent.putExtras(to);
				startActivity(intent);
				break;

			case R.id.btnReminder_CLA:
				BUTTON_ACCESS = REMINDER;
				if(lblRemindTime.getText().toString().equals("")){
					showDialog(TIME_DIALOG);
				}else{
					//Off alarm

					lblRemindTime.setText("");
					
					if(pendingIntent!=null){
						alarmManager.cancel(pendingIntent);
					}	
				}

				break;
			case R.id.btnSubmit_CLA:
				addClass(); 

				break;
			}
		}

	}

	/**
	 * This method set all the subjects in database
	 * into spinner
	 */
	public void setSubjectsListIntoSpin(){
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
							String show = hashSub.get(copyOfSpin.get(position).toString());
							lblSubjects.setText(show);
						}

						public void onNothingSelected(AdapterView<?> adapter) {
							// TODO Auto-generated method stub

						}

					});
				}

			} catch (Exception e) {

				Log.e(TAG+" - doInBackground ", ""+e.getMessage());
				subdatasource.close();
			}
		}
	}

	//Show DatePicker Dialog
	@Override
	protected Dialog onCreateDialog(int id) {

		switch(id){

		case TIME_DIALOG :
			//set current time into timepicker
			return new TimePickerDialog(this, 
					timePickerListener, mHour, mMinutes,false);

		default:
			return null;
		}

	}

	private TimePickerDialog.OnTimeSetListener timePickerListener = 
			new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, 
				int selectedHour, int selectedMinute) {

			// Month is 0 based, just add 1
			final StringBuilder newString =
					new StringBuilder()
			.append(pad(selectedHour))
			.append(":").append(pad(selectedMinute));

			// set current time into textview
			if(BUTTON_ACCESS==REMINDER){
				lblRemindTime.setText(newString.toString());
			}
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}


	/**
	 * This method set all the schedules in database
	 * into spinner
	 */
	public void setScheduleListIntoSpin(){
		if(academic_id>0){

			try {
				schdatasource.open();
				scheList = schdatasource.getAllSchedule(academic_id);
				schdatasource.close();

				if(scheList!=null){

					int size = scheList.size();
					List<String> fromSpin = new ArrayList<String>();
					List<String> toSpin = new ArrayList<String>();

					for(int index=0; index<size; index++){

						String begin = scheList.get(index).getBegin();
						String end = scheList.get(index).getEnd();

						fromSpin.add(begin);
						//String show = begin+"-"+end;
						toSpin.add(end);
					}

					final List<String> copyOfFromSpin = fromSpin;
					final List<String> copyOfToSpin = toSpin;

					ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>
					(this,android.R.layout.simple_spinner_item,copyOfFromSpin);

					ArrayAdapter<String> adapterTo = new ArrayAdapter<String>
					(this,android.R.layout.simple_spinner_item,copyOfToSpin);

					spinTimeFrom.setAdapter(adapterFrom);
					spinTimeTo.setAdapter(adapterTo);
					//Log.d(TAG,"setAdapter pass");

				}

			} catch (Exception e) {

				Log.e(TAG+" - doInBackground ", ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				schdatasource.close();
			}
		}
	}

	public void addClass(){

		String class_title =  txtTitle.getText().toString();
		int selected_index = spinSubjects.getSelectedItemPosition();
		Subjects class_subjects = listSubjects.get(selected_index);
		int selectedPosition = spinWeekday.getSelectedItemPosition();

		int from_index = spinTimeFrom.getSelectedItemPosition();
		Schedule from_schedule =  scheList.get(from_index);
		int to_index = spinTimeTo.getSelectedItemPosition();
		Schedule to_schedule = scheList.get(to_index);
		//int selectedTime =  spinTime.getSelectedItemPosition();
		String alarm = lblRemindTime.getText().toString();

		//validation of input fields
		if(class_title.equals("")
				||selected_index<0
				||selectedPosition<0){

			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();


		}else if(from_index>to_index){
			
			Toast toast = Toast.makeText(this, "Please Verify The Time Setting .", 
					Toast.LENGTH_SHORT);
			toast.show();

			
		}else{
			/**create new object of holiday then 
			 pass it into the data source to 
			 create a new record*/
			Class newClass = new Class();
			newClass.setTitle(class_title);
			newClass.setSubjects_id(class_subjects.getId());
			selectedDay = hashDay.get(selectedPosition);
			newClass.setDay(selectedDay);
			newClass.setTime_from(from_schedule.getId());
			newClass.setTime_to(to_schedule.getId());
			if(alarm.equals("")==false){
				newClass.setAlarm(alarm);
			}else{
				newClass.setAlarm("");
			}

			//	newClass.setSchedule_id(scheList.get(selectedTime).getId());
			newClass.setAcademic_id(academic_id);

			try {

				description= class_title+" - "+class_subjects.getAbbrev();
				
				cladatasource.open();
				Class createdClass = cladatasource.createClass(newClass);
				cladatasource.close();
				createdId = createdClass.getId();
				
				if(alarm.equals("")==false){
					startAlarm();
				}
				Intent classList = new Intent("android.intent.action.CLASSLIST");
				Bundle toClassList = new Bundle();
				toClassList.putLong("academic_id", academic_id);
				classList.putExtras(toClassList);
				startActivity(classList);
				finish();

			} catch (Exception e) {
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				cladatasource.close();
			}			

		}
	}

	/**
	 * Start the alarm 
	 */
	public void startAlarm(){
		/*	
		if(createdId<1){
			addClass();
		}*/
		Log.d(TAG, "Created Class Id: "+createdId);
		thisAlarmID =  AlarmStartCategory+(int)createdId;

		String alarm_time = lblRemindTime.getText().toString();
	
		// Intent for our  BroadcastReceiver 
		Intent intent = new Intent(this, AlarmService.class);
		Bundle bundle = new Bundle();
		bundle.putString("source", "Class");
		bundle.putLong("id", createdId);
		bundle.putInt("key", thisAlarmID);
		bundle.putInt("category", 1);
		bundle.putString("descript", description);
		//bundle.putString("subjects", );
		intent.putExtras(bundle);

		Calendar calNow = Calendar.getInstance();
		//calNow.add(Calendar.DAY_OF_YEAR, 1);
		Calendar calToSet = (Calendar)calNow.clone();

		int pos = alarm_time.indexOf(":");
		int hour = Integer.parseInt(alarm_time.substring(0, pos));
		int minute = Integer.parseInt(alarm_time.substring(pos+1));

		//calToSet.set(Calendar.DAY_OF_WEEK, selectedDay);
		calToSet.set(Calendar.HOUR_OF_DAY, hour);
		calToSet.set(Calendar.MINUTE, minute);
		calToSet.set(Calendar.SECOND, 0);

		int dayNow = calNow.get(Calendar.DAY_OF_WEEK);
		long timesinmsNow = calNow.getTimeInMillis();
		long timetarget = calToSet.getTimeInMillis();
		/**
		 * 
		 * selectedDay = 2(Monday), dayNow =1(Sunday)
		 * dayDiff = 1
		 * selectedDay =1(Sunday) , dayNow =2(Monday)
		 * dayDiff = 5
		 */

		int dayDiff = selectedDay - dayNow;
		if(dayDiff==0){
			if(timesinmsNow>timetarget)
				dayDiff =7;

		}		
		//Calculating day difference
		else if(dayDiff<0){
			dayDiff= 8 - (selectedDay + dayNow);

		}
		
		if(dayDiff>0)
		calToSet.setTimeInMillis(AlarmManager.INTERVAL_DAY*dayDiff);

	/*	Toast.makeText(thisContext, "Day Diff "+dayDiff
				, Toast.LENGTH_LONG).show();*/

		/*long timeSet = timetarget-timesinmsNow;

		if(dayDiff>0){
			timeSet = AlarmManager.INTERVAL_DAY*dayDiff
					-timesinmsNow
					+timetarget;
		}
	
		Log.d(TAG, "TIME SET AT"+timeSet);

		Toast.makeText(thisContext, "Alarm At "+timeSet
				, Toast.LENGTH_LONG).show();
*/
		
		// Set one-time alarm

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// PendingIntent for AlarmManager 
		pendingIntent = PendingIntent.getService(this, thisAlarmID,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calToSet.getTimeInMillis(), pendingIntent);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setSubjectsListIntoSpin();
		setScheduleListIntoSpin();
	}
}
