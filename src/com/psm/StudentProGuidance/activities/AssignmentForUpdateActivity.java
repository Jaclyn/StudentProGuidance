package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.psm.StudentProGuidance.controllers.AssignmentDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Assignment;
import com.psm.StudentProGuidance.entities.Subjects;
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
 * - show the layout ("assignment")
 * - update the selected assignment details
 *
 * Activity FROM
 * - AssignmentListActivity
 * 
 * Activity TO
 * - AssignmentListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * - assignment_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
/**
 * @author Ng Xin Man
 *
 */
public class AssignmentForUpdateActivity extends Activity {

	private EditText txtTitle;
	private Spinner spinSubjects;
	private ImageButton btnAddSubjects;
	private TextView lblSubjectsName;
	private TextView lblRemindTime;
	private ToggleButton btnReminder;
	private TextView txtDescription;
	private Button btnDueDate;
	private Button btnDueTime;
	private Button btnSubmit;
	private AssignmentDataSource assdatasource;
	private SubjectsDataSource subdatasource;
	private final String TAG = "Assignment Update Activity";
	private long academic_id;
	private Context thisContext;
	private long assignment_id;
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
	private Assignment thisAssigmentToUpdate;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private static int AlarmStartCategory = 2000;
	private int thisAlarmID;
	private String description;
	/*
	 (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.assignment);	
		thisContext = this;

		txtTitle = (EditText) findViewById(R.id.txtTitle_AS);
		spinSubjects = (Spinner) findViewById(R.id.spinSubjects_AS);
		btnAddSubjects = (ImageButton) findViewById(R.id.btnAdd_AS);
		lblRemindTime = (TextView) findViewById(R.id.lblReminderChoice_AS);
		lblSubjectsName = (TextView)  findViewById(R.id.lblSubjects2_AS);
		btnReminder = (ToggleButton) findViewById(R.id.btnReminder_AS);
		txtDescription = (EditText) findViewById(R.id.txtDescription_AS);
		btnDueDate = (Button) findViewById(R.id.btnDueDate_AS);
		btnDueTime = (Button) findViewById(R.id.btnDueTime_AS);
		btnSubmit = (Button) findViewById(R.id.btnSubmit_AS);

		assdatasource = new AssignmentDataSource(thisContext);
		subdatasource = new SubjectsDataSource(thisContext);

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");
		assignment_id = from.getLong("assignment_id");

		btnAddSubjects.setOnClickListener(new ButtonOnClickListener());
		btnDueDate.setOnClickListener(new ButtonOnClickListener());
		btnDueTime.setOnClickListener(new ButtonOnClickListener());
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

		if(assignment_id>0){
			findAssignmentRecord();
		}

	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setListIntoSpin();
		findAssignmentRecord();
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
							lblSubjectsName.setText(show);
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

	/**
	 * Find assignment record from database
	 */
	public void findAssignmentRecord(){

		try {

			thisAlarmID =  AlarmStartCategory+(int)assignment_id;
			
			assdatasource.open();		
			Assignment thisAssignment = new Assignment();
			thisAssignment = assdatasource.getCertainAssignment(assignment_id);	
			assdatasource.close();

			/***
			 * If the record of the user is found.
			 * Load it.
			 **/
			if( thisAssignment != null){

				txtTitle.setText(""+thisAssignment.getTitle());

				int index;
				for(index=0; index<listSubjects.size();index++){
					if(listSubjects.get(index).getId()==
							thisAssignment.getSubjects_id())
						break;
				}
				spinSubjects.setSelection(index);

				if(thisAssignment.getDescription()!=null){
					txtDescription.setText(thisAssignment.getDescription());
				}

				btnDueDate.setText(thisAssignment.getDueDate());
				btnDueTime.setText(thisAssignment.getDueTime());
				
				if(thisAssignment.getAlarm()!=null&&thisAssignment.getAlarm().equals("")==false){
					lblRemindTime.setText(thisAssignment.getAlarm());
					btnReminder.setChecked(true);
					Intent intent = new Intent(this, AlarmService.class);
					Bundle bundle = new Bundle();
					bundle.putString("source", "Assignment");
					bundle.putLong("id", assignment_id);
					bundle.putInt("key", thisAlarmID);
					bundle.putInt("category", 2);
					bundle.putString("descript", description);
					intent.putExtras(bundle);
					pendingIntent = PendingIntent.getService(this, thisAlarmID,
							intent, PendingIntent.FLAG_CANCEL_CURRENT);
					//Log.d(TAG,"LOAD ALARM");
				}

			}


		} catch (Exception e) {

			Log.e(TAG+" - "+"findAssignmentRecord ", ""+e.getMessage());
			assdatasource.close();

		}

	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnAdd_AS :
				Intent intent = new Intent("android.intent.action.SUBJECTSLIST");
				Bundle to = new Bundle();
				to.putLong("academic_id", academic_id);
				intent.putExtras(to);
				startActivity(intent);
				break;

			case R.id.btnDueDate_AS :
				showDialog(DATE_DIALOG);
				break;

			case R.id.btnDueTime_AS :
				BUTTON_ACCESS = TIMESET;
				showDialog(TIME_DIALOG);
				break;

			case R.id.btnReminder_AS:
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
				
			case  R.id.btnSubmit_AS :
				updateAssignment();
				break;


			}
		}
	}

	/**
	 * update lecturer record
	 */
	public void updateAssignment(){

		String title = txtTitle.getText().toString();
		int selectedIndex = spinSubjects.getSelectedItemPosition();
		//String[] description_split = txtDescription.getText().toString().split("\n");
		String duedate = btnDueDate.getText().toString();
		String duetime = btnDueTime.getText().toString();
		String extra = txtDescription.getText().toString();
		String alarm = lblRemindTime.getText().toString();

		//Create a string objects for multiple line description
		//to put in database
		/*for(int line=0; line<description_split.length; line++){
			description.append(description_split[line]);

			if(line!=description_split.length){
				description.append("\n");
			}
		}
*/
		if(title.equals("")
				||selectedIndex<0
				||duedate.equals(getString(R.string.pickOneDate))
				||duetime.equals(getString(R.string.pickOneTime))){

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Assignment");
			alertDialog.setMessage("Please Verify Your Input Fields Before Proceed.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {  
					return;  
				} });   
			alertDialog.show();

		}else{


			thisAssigmentToUpdate = new Assignment();
			thisAssigmentToUpdate.setTitle(title);
			thisAssigmentToUpdate.setSubjects_id(listSubjects.get(selectedIndex).getId());
			thisAssigmentToUpdate.setDescription(extra);
			thisAssigmentToUpdate.setDueDate(duedate);
			thisAssigmentToUpdate.setDueTime(duetime);
			if(alarm.equals("")==false){
				thisAssigmentToUpdate.setAlarm(alarm);
			}		else{
				thisAssigmentToUpdate.setAlarm("");
			}
			thisAssigmentToUpdate.setAcademic_id(academic_id);
			thisAssigmentToUpdate.setId(assignment_id);

			description= title+" - "+listSubjects.get(selectedIndex).getAbbrev();
			
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
	 * details of this lecturer.
	 */
	public void updateProgress(){
		String alarm = lblRemindTime.getText().toString();
			try {
				assdatasource.open();
				assdatasource.updateAssignment(thisAssigmentToUpdate);			
				assdatasource.close();		
				
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
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("Error");
				alert.setMessage(""+e.getMessage());
				alert.setButton("OK", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) {  
						return;  
					} });   
				alert.show();
				assdatasource.close();

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
				
				btnDueDate.setText(newString);
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
					btnDueTime.setText(newString.toString());
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
			thisAlarmID =  AlarmStartCategory+(int)assignment_id;

			String alarm_time = lblRemindTime.getText().toString();

			// Intent for our  BroadcastReceiver 
			Intent intent = new Intent(this, AlarmService.class);
			Bundle bundle = new Bundle();
			bundle.putString("source", "Assignment");
			bundle.putLong("id", assignment_id);
			bundle.putInt("key", thisAlarmID);
			bundle.putInt("category", 2);
			bundle.putString("descript", description);
			intent.putExtras(bundle);

			Calendar calNow = Calendar.getInstance();
			//calNow.add(Calendar.DAY_OF_YEAR, 1);
			Calendar calToSet = (Calendar)calNow.clone();

			int pos = alarm_time.indexOf(":");
			int hour = Integer.parseInt(alarm_time.substring(0, pos));
			int minute = Integer.parseInt(alarm_time.substring(pos+1));

			String date  = btnDueDate.getText().toString();
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
				calToSet.setTimeInMillis(calToSet.getTimeInMillis()+AlarmManager.INTERVAL_DAY*dayDiff);

			if(calToSet.before(calNow)){
				Toast.makeText(thisContext, 
						"Inproper Reminder Time"
			, Toast.LENGTH_LONG).show();
				btnReminder.setChecked(false);
				lblRemindTime.setText("");

				try {
					assdatasource.open();
					assdatasource.setAlarmNull(assignment_id);
					assdatasource.close();
				/*	Toast.makeText(this, "Alarm Destroy"
							, Toast.LENGTH_LONG).show();*/
				} catch (Exception e) {
					Log.e("Alarm OnDestroy", ""+e.getMessage());
					assdatasource.close();
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
