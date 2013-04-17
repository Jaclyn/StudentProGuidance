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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
 * - connect to database and display the selected records
 * - enable update
 * 
 * Activity FROM
 * - ClassListActivity
 * 
 * Activity TO
 * - SubjectsListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * - class_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class ClassForUpdateActivity extends Activity {

	private Spinner spinSubjects;
	private EditText txtTitle;
	private TextView lblSubjects;
	private Spinner spinWeekday;
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
	private long class_id;
	private final String TAG = "CLASS FOR UPDATE ACTIVITY";
	private List<Subjects> listSubjects;
	private Hashtable <String,String> hashSub 
	= new Hashtable<String,String>();
	private Hashtable <Integer,Integer> hashDay 
	= new Hashtable<Integer,Integer>();
	private Context thisContext;
	private ToggleButton btnReminder;
	private TextView lblRemindTime;
	private int mHour;
	private int mMinutes;
	private final int TIME_DIALOG = 1;
	private final int REMINDER = 0;
	private int BUTTON_ACCESS = 0;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private ArrayAdapter<String> weekdayAdapter;
	private List<Integer> daySet;
	private int AlarmStartCategory = 1000;
	private int thisAlarmID;
	private int selectedDay;
	private String description;
	private Class newClass;

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
		btnReminder = (ToggleButton) findViewById(R.id.btnReminder_CLA);
		spinTimeFrom = (Spinner) findViewById(R.id.spinTimeFrom_CLA);
		spinTimeTo = (Spinner) findViewById(R.id.spinTimeTo_CLA);
		btnAdd = (ImageButton) findViewById(R.id.btnAdd_CLA);
		btnSubmit = (Button) findViewById(R.id.btnSubmit_CLA);

		btnAdd.setOnClickListener(new ButtonOnClickListener());
		btnSubmit.setOnClickListener(new ButtonOnClickListener());
		btnReminder.setOnClickListener(new ButtonOnClickListener());

		//Get current System date
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinutes = c.get(Calendar.MINUTE);

		//access database
		acadatasource = new AcademicDataSource(this);
		cladatasource = new ClassDataSource(this);
		schdatasource = new ScheduleDataSource(this);
		subdatasource = new SubjectsDataSource(this);

		weekday = new ArrayList<String>();
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		schdatasource.open();
		scheList = schdatasource.getAllSchedule(academic_id);
		schdatasource.close();

		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");
		class_id = bundle.getLong("class_id");

		setSubjectsListIntoSpin();
		setScheduleListIntoSpin();
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
						index++;
					}
				}

				weekdayAdapter = new ArrayAdapter<String>
				(this,android.R.layout.simple_spinner_item,weekday);

				spinWeekday.setAdapter(weekdayAdapter);

				setSubjectsListIntoSpin();
				setScheduleListIntoSpin();

				if(class_id>0){
					thisAlarmID = AlarmStartCategory + (int)class_id;
					loadDatabase();
					Log.d(TAG, "Pass Load Database()");	
				}

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
				updateClass();

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
				Log.d(TAG,"SUBJECTS SET");

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
					Log.d(TAG,"SCHEDULES SET");

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

	/**
	 * This method is to load database details into view
	 */
	public void loadDatabase(){

		try {

			cladatasource.open();		
			Class thisClass = new Class();
			thisClass = cladatasource.getCertainClass(class_id);	
			cladatasource.close();

			Log.d(TAG,"thisClass = "+thisClass);
			/***
			 * If the record of the user is found.
			 * Load it.
			 **/
			if( thisClass != null){

				txtTitle.setText(""+thisClass.getTitle());

				Subjects thisSubjects = new Subjects();
				int index=0;
				for(index=0; index<listSubjects.size();index++){
					if(listSubjects.get(index).getId()==
							thisClass.getSubjects_id()){
						thisSubjects = listSubjects.get(index);
						break;
					}
				}
				spinSubjects.setSelection(index);

				Log.d(TAG,"LOAD SUBJECTS");
				int day=thisClass.getDay();

				int indexDay=0;
				for(indexDay=0; indexDay<hashDay.size();indexDay++){
					if(hashDay.get(indexDay)==day){
						spinWeekday.setSelection(indexDay);
						break;
					}
				}
				Log.d(TAG,"LOAD DAY");

				int indexFrom=0;
				for(indexFrom=0; indexFrom<scheList.size();indexFrom++){
					if(scheList.get(indexFrom).getId()==
							thisClass.getTime_from())
						break;
				}
				if(indexFrom<scheList.size())
					spinTimeFrom.setSelection(indexFrom);
				Log.d(TAG,"LOAD FROM TIME");

				int indexTo=0;
				for(indexTo=0; indexTo<scheList.size();indexTo++){
					if(scheList.get(indexTo).getId()==
							thisClass.getTime_to())
						break;
				}
				if(indexTo<scheList.size())
					spinTimeTo.setSelection(indexTo);
				Log.d(TAG,"LOAD TO TIME");

				description= thisClass.getTitle()+" - "+thisSubjects.getAbbrev();


				//Log.d(TAG,"spinWeekday SET + POS "+thisKey );
				if(thisClass.getAlarm()!=null && thisClass.getAlarm().equals("")==false){
					lblRemindTime.setText(thisClass.getAlarm());
					btnReminder.setChecked(true);
					Intent intent = new Intent(this, AlarmService.class);
					Bundle bundle = new Bundle();
					bundle.putString("source", "Class");
					bundle.putLong("id", thisClass.getId());
					bundle.putInt("key", thisAlarmID);
					bundle.putInt("category", 1);
					bundle.putString("descript", description);
					intent.putExtras(bundle);
					pendingIntent = PendingIntent.getService(this, thisAlarmID,
							intent, PendingIntent.FLAG_CANCEL_CURRENT);
					Log.d(TAG,"LOAD ALARM");
				}

			}


		} catch (Exception e) {

			Log.d(TAG, ""+e.getMessage());
			Toast toast = Toast.makeText(this, "LoadDatabase : "+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			cladatasource.close();

		}

	}

	/**
	 * This method is to update class details
	 */
	public void updateClass(){

		String class_title =  txtTitle.getText().toString();
		int selected_index = spinSubjects.getSelectedItemPosition();
		Subjects class_subjects = listSubjects.get(selected_index);
		int selectedPosition = spinWeekday.getSelectedItemPosition();

		int from_index = spinTimeFrom.getSelectedItemPosition();
		Schedule from_schedule =  scheList.get(from_index);
		int to_index = spinTimeTo.getSelectedItemPosition();
		Schedule to_schedule = scheList.get(to_index);
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
			/**create new object of class then 
			 pass it into the data source to 
			 create a new record*/
			newClass = new Class();
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
			newClass.setAcademic_id(academic_id);
			newClass.setId(class_id);
			try {

				description= class_title+" - "+class_subjects.getAbbrev();

				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("Confirmation");
				alert.setMessage("Confirm Update?");
				alert.setButton("Yes", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) { 
						//click = true;
						upgradeClass();
						return;
					} });   
				alert.setButton2("No", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) {  
						return;  
					} });  
				alert.show();

			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				cladatasource.close();
			}

		}
	}

	public void upgradeClass(){

		String alarm = lblRemindTime.getText().toString();
		try {
			cladatasource.open();
			cladatasource.updateClass(newClass);
			cladatasource.close();

			if(alarm.equals("")==false){
				startAlarm();
			}else{

				cladatasource.open();
				cladatasource.setAlarmNull(class_id);
				cladatasource.close();
				/*Toast.makeText(this, "Alarm Destroy"
						, Toast.LENGTH_LONG).show();*/
			}

			Toast toast = Toast.makeText(this, "Successfully Updated!", 
					Toast.LENGTH_SHORT);
			toast.show();
		} catch (Exception e) {
			Log.e("Updating Class", ""+e.getMessage());
			cladatasource.close();
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
		Log.d(TAG, "Created Class Id: "+class_id);
		thisAlarmID =  AlarmStartCategory+(int)class_id;

		String alarm_time = lblRemindTime.getText().toString();

		// Intent for our  BroadcastReceiver 
		Intent intent = new Intent(this, AlarmService.class);
		Bundle bundle = new Bundle();
		bundle.putString("source", "Class");
		bundle.putLong("id", class_id);
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
