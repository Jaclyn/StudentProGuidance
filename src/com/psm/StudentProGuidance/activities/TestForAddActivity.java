/**
 * 
 */
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
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This class will 
 * - show the layout ("test")
 * - connect to database and create new record of subjects
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
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class TestForAddActivity extends Activity {

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

	private final String TAG = "Test Add Activity";
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
	private long academic_id;
	private List<Subjects> listSubjects;
	private Hashtable <String,String> hashSub 
	= new Hashtable<String,String>();
	private Context thisContext;
	private Bundle from;
	private List<String> chapterList = new ArrayList<String>();
	private long createdId;
	private static int AlarmStartCategory = 3000;
	private int thisAlarmID;
	private String description;

	/* (non-Javadoc)
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
		btnReminder = (ToggleButton) findViewById(R.id.btnReminder_T);
		btnDate = (Button) findViewById(R.id.btnDate_T);
		btnTime = (Button) findViewById(R.id.btnTime_T);
		btnSubmit = (Button) findViewById(R.id.btnSubmit_T);

		btnAddSubjects.setOnClickListener(new ButtonOnClickListener());
		btnDate.setOnClickListener(new ButtonOnClickListener());
		btnTime.setOnClickListener(new ButtonOnClickListener());
		btnReminder.setOnClickListener(new ButtonOnClickListener());
		btnSubmit.setOnClickListener(new ButtonOnClickListener());

		//Get current System date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinutes = c.get(Calendar.MINUTE);

		subdatasource = new SubjectsDataSource(this);
		testdatasource = new TestDataSource(this);

		from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");

		Log.d(TAG,"academic_id "+academic_id);

		setListIntoSpin();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setListIntoSpin();
	}

	/**
	 * This method set all the subjects  in database
	 * & chapter into spinner
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

							for(index=1;index<=totalchapter;index++){
								String chapter = String.valueOf(index);
								chapterList.add(chapter);
							}

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
				handleBtnAddClicked();
				break;

			}
		}
	}

	/**
	 * This method handles the event when the 
	 * button add assignment is clicked.
	 */
	public void handleBtnAddClicked(){

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

			Test test = new Test();
			test.setTitle(title);
			test.setSubjects_id(listSubjects.get(selectedIndexSubjects).getId());
			String from = chapterList.get(selectedIndexFrom);
			String to = chapterList.get(selectedIndexTo);
			String chapter = from+"-"+to;
			test.setChapter(chapter);
			test.setVenue(venue);
			test.setDate(date);
			test.setTime(time);
			if(alarm.equals("")==false){
				test.setAlarm(alarm);
			}else{
				test.setAlarm("");
			}
			test.setAcademic_id(academic_id);

			try {
				description= title+" - "+listSubjects.get(selectedIndexSubjects).getAbbrev();

				Test createdTest = testdatasource.createTest(test);
				createdId = createdTest.getId();
				testdatasource.close();
				
				if(alarm.equals("")==false){
					startAlarm();
					/*****************
					 * startAlarm();**
					 *****************/
				}		
				
				Intent intent = new Intent("android.intent.action.TESTLIST");
				Bundle bundle = new Bundle();
				bundle.putLong("academic_id", academic_id);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();


			} catch (Exception e) {

				Log.e(TAG+" - "+"handleBtnAddClicked ", ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				testdatasource.close();
			}

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
		thisAlarmID =  AlarmStartCategory+(int)createdId;

		String alarm_time = lblRemindTime.getText().toString();

		// Intent for our  BroadcastReceiver 
		Intent intent = new Intent(this, AlarmService.class);
		Bundle bundle = new Bundle();
		bundle.putString("source", "Test");
		bundle.putLong("id", createdId);
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
				testdatasource.setAlarmNull(createdId);
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
