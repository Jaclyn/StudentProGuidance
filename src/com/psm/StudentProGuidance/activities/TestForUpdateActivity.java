package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.controllers.TestDataSource;
import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.entities.Test;
import com.psm.StudentProGuidance.others.AlarmService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * This class will 
 * - show the layout ("test")
 * - update the selected test details
 *
 * Activity FROM
 * - TestListActivity
 * 
 * Activity TO
 * - SubjectsListActivity
 * - TestListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * - test_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class TestForUpdateActivity extends Activity {
	private EditText txtTitle;
	private Spinner spinSubjects;
	private ImageButton btnAddSubjects;
	private TextView lblSubjects;
	private TextView lblRemindTime;
	private ToggleButton btnReminder;
	private Spinner spinFrom;
	private Spinner spinTo;
	private EditText txtVenue;
	private Button btnDate;
	private Button btnTime;
	private Button btnSubmit;
	private SubjectsDataSource subdatasource;
	private TestDataSource testdatasource;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private final String TAG = "Test Update Activity";
	private long academic_id;
	private Context thisContext;
	private long test_id;
	private final int DATE_DIALOG = 0;
	private final int TIME_DIALOG = 1;
	private int BUTTON_ACCESS = 0;
	private final int REMINDER = 1;
	private final int TIMESET = 2;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinutes;
	private List<Subjects> listSubjects;
	private Hashtable <String,String> hashSub 
	= new Hashtable<String,String>();
	private Test thisTestToUpdate;
	private List<String> chapterList = new ArrayList<String>();
	private static int AlarmStartCategory = 3000;
	private int thisAlarmID;
	private String description;

	/*
	 (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);	
		thisContext = this;

		txtTitle = (EditText) findViewById(R.id.txtTitle_T);
		spinSubjects = (Spinner) findViewById(R.id.spinSubjects_T);
		btnAddSubjects = (ImageButton) findViewById(R.id.btnAdd_T);
		lblSubjects = (TextView) findViewById(R.id.lblSubjects2_T);
		lblRemindTime = (TextView) findViewById(R.id.lblReminderChoice_T);
		spinFrom = (Spinner)  findViewById(R.id.spinFrom_T);
		spinTo = (Spinner) findViewById(R.id.spinTo_T);
		txtVenue = (EditText) findViewById(R.id.txtVenue_T);
		btnDate = (Button) findViewById(R.id.btnDate_T);
		btnReminder = (ToggleButton) findViewById(R.id.btnReminder_T);
		btnTime = (Button) findViewById(R.id.btnTime_T);
		btnSubmit = (Button) findViewById(R.id.btnSubmit_T);

		subdatasource = new SubjectsDataSource(this);
		testdatasource = new TestDataSource(this);

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");
		test_id = from.getLong("test_id");
		Log.d(TAG,"academic_id "+academic_id);
		Log.d(TAG,"test_id "+test_id);

		btnAddSubjects.setOnClickListener(new ButtonOnClickListener());
		btnDate.setOnClickListener(new ButtonOnClickListener());
		btnTime.setOnClickListener(new ButtonOnClickListener());
		btnReminder.setOnClickListener(new ButtonOnClickListener());
		btnSubmit.setOnClickListener(new ButtonOnClickListener());
		
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		//Get current System date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinutes = c.get(Calendar.MINUTE);

		setListIntoSpin();

		if(test_id>0){
			findTestRecord();
		}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setListIntoSpin();
		if(test_id>0){
			findTestRecord();
		}
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
							String show = hashSub.get(copyOfSpin.get(position).toString());
							lblSubjects.setText(show);
							//change the list in the spinFrom & spinTo
							int totalchapter = listSubjects.get(position).getTotalChapter();
							int index;
							if(chapterList.size()>0){
								chapterList.clear();
							}
							for(index=1;index<=totalchapter;index++){
								String chapter = String.valueOf(index);
								chapterList.add(chapter);
							}

							Log.d(TAG, "Setting Spin Subjects-> chapterList size : "+chapterList.size());
							
							ArrayAdapter<String> chapterAdapter = new ArrayAdapter<String>
							(thisContext,android.R.layout.simple_spinner_item,chapterList);
							spinFrom.setAdapter(chapterAdapter);
							spinTo.setAdapter(chapterAdapter);
						}

						public void onNothingSelected(AdapterView<?> adapter) {
							// TODO Auto-generated method stub

						}

					});
				}

			} catch (Exception e) {

				Log.e(TAG+" - doInBackground ", ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				subdatasource.close();
			}
		}
	}

	public void findTestRecord(){

		try {

			testdatasource.open();		
			Test thisTest = new Test();
			thisTest = testdatasource.getCertainTest(test_id);	
			testdatasource.close();

			Log.d(TAG,"thisTest = "+thisTest);
			/***
			 * If the record of the user is found.
			 * Load it.
			 **/
			if( thisTest != null){

				txtTitle.setText(""+thisTest.getTitle());

				int index;
				for(index=0; index<listSubjects.size();index++){
					if(listSubjects.get(index).getId()==
							thisTest.getSubjects_id()){
						spinSubjects.setSelection(index);
						break;
					}
				}
				Log.d(TAG, "index Subjects: "+index);
				
				subdatasource.open();
				Subjects thisSub = subdatasource.getCertainSubjects(thisTest.getSubjects_id());
				subdatasource.close();
				int totalchapter = thisSub.getTotalChapter();
				if(chapterList.size()>0){
					chapterList.clear();
				}
				for(int loop=1;loop<=totalchapter;loop++){
					String chapter = String.valueOf(loop);
					chapterList.add(chapter);
				}

				
				String chapterRange = thisTest.getChapter();
				Log.d(TAG, "chapterRange : "+chapterRange);
				String[] chap = chapterRange.split("-");
				Log.d(TAG, "chapterList size : "+chapterList.size());
				
				
				int index2;
				for(index2=0; index2<chapterList.size();index2++){
					if(chapterList.get(index2).equals(chap[0])){
						spinFrom.setSelection(index2);
						break;
					}
				}
				//Log.d(TAG, "index From: "+index2);
			
				int index3;
				for(index3=0; index3<chapterList.size();index3++){
					if(chapterList.get(index3).equals(chap[1])){
						spinTo.setSelection(index3);
						break;
					}
				}
				//Log.d(TAG, "index To: "+index3);
				
				txtVenue.setText(thisTest.getVenue());
				btnDate.setText(thisTest.getDate());
				btnTime.setText(thisTest.getTime());

				if(thisTest.getAlarm()!=null&&thisTest.getAlarm().equals("")==false){
					lblRemindTime.setText(thisTest.getAlarm());
					btnReminder.setChecked(true);
					Intent intent = new Intent(this, AlarmService.class);
					Bundle bundle = new Bundle();
					bundle.putString("source", "Test");
					bundle.putLong("id", test_id);
					bundle.putInt("key", thisAlarmID);
					bundle.putInt("category", 3);
					bundle.putString("descript", description);
					intent.putExtras(bundle);
					pendingIntent = PendingIntent.getService(this, thisAlarmID,
							intent, PendingIntent.FLAG_CANCEL_CURRENT);
				}
			}


		} catch (Exception e) {

			Log.e(TAG+" - "+"findTestRecord ", ""+e.getLocalizedMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			testdatasource.close();
			subdatasource.close();
		}

	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnAdd_T :
				Intent intent = new Intent("android.intent.action.SUBJECTSLIST");
				Bundle to = new Bundle();
				to.putLong("academic_id", academic_id);
				intent.putExtras(to);
				startActivity(intent);
				break;

			case R.id.btnDate_T :
				showDialog(DATE_DIALOG);
				break;

			case R.id.btnTime_T :
				BUTTON_ACCESS = TIMESET;
				showDialog(TIME_DIALOG);
				break;

			case R.id.btnReminder_T:
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
				
			case  R.id.btnSubmit_T :
				updateTest();
				break;


			}
		}
	}

	/**
	 * update test record
	 */
	public void updateTest(){

		String title = txtTitle.getText().toString();
		int selectedIndexSubjects = spinSubjects.getSelectedItemPosition();
		int selectedIndexFrom = spinFrom.getSelectedItemPosition();
		int selectedIndexTo = spinTo.getSelectedItemPosition();
		String venue = txtVenue.getText().toString();
		String date = btnDate.getText().toString();
		String time = btnTime.getText().toString();
		String alarm = lblRemindTime.getText().toString();

		if(title.equals("")
				||selectedIndexSubjects<0
				||selectedIndexFrom<0
				||selectedIndexTo<0
				||date.equals(getString(R.string.pickOneDate))
				||time.equals(getString(R.string.pickOneTime))){

			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();

		}else if(selectedIndexFrom>selectedIndexTo){

			Toast toast = Toast.makeText(this, "Please Select Proper Chapter Range.", 
					Toast.LENGTH_SHORT);
			toast.show();


		}else{

			testdatasource.open();

			thisTestToUpdate = new Test();
			thisTestToUpdate.setTitle(title);
			thisTestToUpdate.setSubjects_id(listSubjects.get(selectedIndexSubjects).getId());
			String from = chapterList.get(selectedIndexFrom);
			String to = chapterList.get(selectedIndexTo);
			String chapter = from+"-"+to;
			thisTestToUpdate.setChapter(chapter);
			thisTestToUpdate.setVenue(venue);
			thisTestToUpdate.setDate(date);
			thisTestToUpdate.setTime(time);
			if(alarm.equals("")==false){
				thisTestToUpdate.setAlarm(alarm);
				
			}		else{
				thisTestToUpdate.setAlarm("");
			}
			thisTestToUpdate.setAcademic_id(academic_id);
			thisTestToUpdate.setId(test_id);
			
			description= title+" - "+listSubjects.get(selectedIndexSubjects).getAbbrev();

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
	 * details of this test.
	 */
	public void updateProgress(){
		
		String alarm = lblRemindTime.getText().toString();

		try {
			testdatasource.open();
			testdatasource.updateTest(thisTestToUpdate);			
			testdatasource.close();		
			if(alarm.equals("")==false){
				startAlarm();
				/*****************
				 * startAlarm();**
				 *****************/
			}
		
			Toast toast = Toast.makeText(this, "Successfully Update!", 
					Toast.LENGTH_SHORT);
			toast.show();

		} catch (Exception e) {

			Log.e(TAG, ""+e.getMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			testdatasource.close();

		}
	}

	//Show DatePicker Dialog
	@Override
	protected Dialog onCreateDialog(int id) {

		switch(id){

		case DATE_DIALOG :
			//set current date into datepicker
			return new DatePickerDialog(this,
					dateSetListener,
					mYear, mMonth, mDay);

		case TIME_DIALOG :
			//set current time into timepicker
			return new TimePickerDialog(this, 
					timePickerListener, mHour, mMinutes,false);

		default:
			return null;
		}

	}

	private DatePickerDialog.OnDateSetListener dateSetListener =
			new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear, 
				int selectedMonthOfYear, int selectedDayOfMonth) {

			// Month is 0 based, just add 1
			final StringBuilder newString =
					new StringBuilder()
			.append(selectedDayOfMonth).append("/")
			.append(selectedMonthOfYear + 1).append("/")
			.append(selectedYear);
		
			btnDate.setText(newString);
		}
	};

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
			}else{
				btnTime.setText(newString.toString());
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
	 * Start the alarm 
	 */
	public void startAlarm(){
		/*	
		if(createdId<1){
			addClass();
		}*/
		thisAlarmID =  AlarmStartCategory+(int)test_id;

		String alarm_time = lblRemindTime.getText().toString();

		// Intent for our  BroadcastReceiver 
		Intent intent = new Intent(this, AlarmService.class);
		Bundle bundle = new Bundle();
		bundle.putString("source", "Test");
		bundle.putLong("id", test_id);
		bundle.putInt("key", thisAlarmID);
		bundle.putInt("category", 3);
		bundle.putString("descript", description);
		intent.putExtras(bundle);

		Calendar calNow = Calendar.getInstance();
		//calNow.add(Calendar.DAY_OF_YEAR, 1);
		Calendar calToSet = (Calendar)calNow.clone();

		int pos = alarm_time.indexOf(":");
		int hour = Integer.parseInt(alarm_time.substring(0, pos));
		int minute = Integer.parseInt(alarm_time.substring(pos+1));

		String date  = btnDate.getText().toString();
		String dateSplit[] = date.split("/");
		int day = Integer.parseInt(dateSplit[0]);
		int month = Integer.parseInt(dateSplit[1]);
		int year = Integer.parseInt(dateSplit[2]);

		calToSet.set(Calendar.DAY_OF_MONTH, day);
		calToSet.set(Calendar.MONTH, month-1);
		calToSet.set(Calendar.YEAR, year);
		calToSet.set(Calendar.HOUR_OF_DAY, hour);
		calToSet.set(Calendar.MINUTE, minute);
		calToSet.set(Calendar.SECOND, 0);

		Date dateNow = calNow.getTime();
		Date dateToSet = calToSet.getTime();

		int dayDiff = Days.daysBetween
				(new DateTime(dateNow), new DateTime(dateToSet))
				.getDays();

		if(dayDiff>0)
			calToSet.setTimeInMillis(calToSet.getTimeInMillis()
					+AlarmManager.INTERVAL_DAY*dayDiff);
		
		if(calToSet.before(calNow)){
			Toast.makeText(thisContext, 
					"Inproper Reminder Time"
		, Toast.LENGTH_LONG).show();
			btnReminder.setChecked(false);
			lblRemindTime.setText("");
			try {
				testdatasource.open();
				testdatasource.setAlarmNull(test_id);
				testdatasource.close();
			/*	Toast.makeText(this, "Alarm Destroy"
						, Toast.LENGTH_LONG).show();*/
			} catch (Exception e) {
				Log.e("Alarm OnDestroy", ""+e.getMessage());
				testdatasource.close();
			}
			
		}else{
			// Set one-time alarm
			alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			// PendingIntent for AlarmManager 
			pendingIntent = PendingIntent.getService(this, thisAlarmID,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarmManager.set(AlarmManager.RTC_WAKEUP, calToSet.getTimeInMillis(), pendingIntent);
		}
	}

}
