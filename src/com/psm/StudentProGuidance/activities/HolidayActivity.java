package com.psm.StudentProGuidance.activities;

import java.util.Calendar;

import com.psm.StudentProGuidance.controllers.HolidayDataSource;
import com.psm.StudentProGuidance.entities.Holiday;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * This class will 
 * - show layout ("holiday")
 * - connect to database and create new record of holiday
 * 
 * Activity FROM
 * - HolidayListActivity
 * 
 * Activity BACK
 * - HolidayListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class HolidayActivity extends Activity{

	private EditText title;
	private RadioGroup rgType;
	private Button btnOneDate;
	private Button btnStartDate;
	private Button btnEndDate;
	private Button btnAdd;
	private Calendar mOneDate= Calendar.getInstance();;
	private Calendar mStartDate= Calendar.getInstance();;
	private Calendar mEndDate= Calendar.getInstance();;
	private int typeIndicator=0;
	static final int ONE_DATE = 0;
	static final int DURATIONSTART = 1;
	static final int DURATIONEND = 2;
	static final int ADD = 3;
	static final int FINISH = 4;
	static final String DEFAULT = "-";
	public static final String TAG = "HOLIDAY ACTIVITY";
	public static final String ADD_ACADEMIC = "add";
	public static final String UPDATE_ACADEMIC = "update";
	private int cYear,cMonth,cDay;
	private int year, month, day;
	private ViewSwitcher switcher;
	private int type = 0;
	private HolidayDataSource holidaydatasource;
	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.holiday);

		Log.d(TAG, "IM in Holiday Activity");

		title = (EditText) findViewById(R.id.txtTitle_H);
		rgType = (RadioGroup) findViewById(R.id.rgHolType_H);
		btnOneDate = (Button) findViewById(R.id.btnPickOneDate_H);
		btnStartDate = (Button) findViewById(R.id.btnPickDuraStartDate_H);
		btnEndDate = (Button) findViewById(R.id.btnPickDuraEndDate_H);
		switcher = (ViewSwitcher) findViewById(R.id.switcher_H);
		btnAdd = (Button) findViewById(R.id.btnAdd_H);

		//Get current System date
		final Calendar c = Calendar.getInstance();
		cYear = c.get(Calendar.YEAR);
		cMonth = c.get(Calendar.MONTH);
		cDay = c.get(Calendar.DAY_OF_MONTH);

		rgType.setOnCheckedChangeListener(CC);
		btnOneDate.setOnClickListener(new ButtonOnClickListener());
		btnStartDate.setOnClickListener(new ButtonOnClickListener());
		btnEndDate.setOnClickListener(new ButtonOnClickListener());
		btnAdd.setOnClickListener(new ButtonOnClickListener());

		holidaydatasource = new HolidayDataSource(this);

		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");

		Log.d(TAG, "academic_id : "+academic_id);

	}

	/**
	 * Switch the view regarding the selected radio button 
	 */
	private RadioGroup.OnCheckedChangeListener CC = new RadioGroup.OnCheckedChangeListener(){

		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int checkedRadioButton = rgType.getCheckedRadioButtonId();

			switch (checkedRadioButton) {
			case R.id.rbOnce_H : 
				type = 0;
				switcher.showPrevious();
				break;

			case R.id.rbDuration_H :
				type = 1;
				switcher.showNext();
				break;
			}
		}
	};


	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnPickOneDate_H :
				typeIndicator = ONE_DATE;
				showDialog(ONE_DATE);
				break;

			case R.id.btnPickDuraStartDate_H :
				typeIndicator = DURATIONSTART;
				showDialog(DURATIONSTART);
				break;

			case R.id.btnPickDuraEndDate_H :
				typeIndicator = DURATIONEND;
				showDialog(DURATIONEND);
				break;

			case  R.id.btnAdd_H :
				addHoliday();
				break;

			}
		}
	}

	/**
	 * add new holiday record
	 */
	public void addHoliday(){

		String hol_title = (String) title.getText().toString();
		String hol_oneDate = (String) btnOneDate.getText().toString();
		String hol_startDate = (String) btnStartDate.getText().toString();
		String hol_endDate = (String) btnEndDate.getText().toString();

		Log.d(TAG, "type : "+type);
		//FOR TYPE ONE DATE ONLY
		if(type==0){
			//validation of input fields
			if(hol_title.equals("")
					||hol_oneDate.equals(getString(R.string.pickOneDate))){

				Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
						Toast.LENGTH_SHORT);
				toast.show();
				
			}else{

				handleItems();
			}

			//FOR TYPE DURATION
		}else{
			
			//validation of input fields
			if(hol_title.equals("")
					||hol_startDate.equals(getString(R.string.txtStart))
					||hol_endDate.equals(getString(R.string.txtEnd))){

				Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
						Toast.LENGTH_SHORT);
				toast.show();
				
			}else{
			
				/***
				 * DEBUG
				 * 
				 * -Calendar is convert before valid input
				 */
				Calendar cStart = Calendar.getInstance();
				String[]startDate = hol_startDate.split("/");
				int startDay  = Integer.parseInt(startDate[0])+1;
				int startMonth  = Integer.parseInt(startDate[1]);
				int startYear  = Integer.parseInt(startDate[2]);
				cStart.set(startYear, startMonth, startDay);
				
				Calendar cEnd = Calendar.getInstance();
				String[]endDate = hol_endDate.split("/");
				int endDay  = Integer.parseInt(endDate[0]);
				int endMonth  = Integer.parseInt(endDate[1]);
				int endYear  = Integer.parseInt(endDate[2]);
				cEnd.set(endYear, endMonth, endDay);
				
				if(cEnd.before(cStart)){
					
					Toast toast = Toast.makeText(this, "Please Verify The Date Input.", Toast.LENGTH_SHORT);
					toast.show();
					
				}else{
					handleItems();
				}
			}
		}

	}

	/**
	 * Create and Load Holidays records
	 * after validation
	 */
	public void handleItems(){

		String hol_title = (String) title.getText().toString();
		String hol_oneDate = (String) btnOneDate.getText().toString();
		String hol_startDate = (String) btnStartDate.getText().toString();
		String hol_endDate = (String) btnEndDate.getText().toString();
		int hol_type = type;

		/**create new object of holiday then 
		 pass it into the data source to 
		 create a new record*/
		Holiday holiday = new Holiday();
		holiday.setTitle(hol_title);
		holiday.setType(hol_type);
		if (type == 0){
			holiday.setStartdate(hol_oneDate);
			holiday.setEnddate(DEFAULT);
		}else{
			holiday.setStartdate(hol_startDate);
			holiday.setEnddate(hol_endDate);
		}

		holiday.setAcademic_id(academic_id);

		try {
			holidaydatasource.open();

			holidaydatasource.createHoliday(holiday);

			Log.d(TAG, "successfully added");

			holidaydatasource.close();

			//reload and go to the holiday list view
			Intent holidaylist = new Intent("android.intent.action.HOLIDAYLIST");
			Bundle bundle = new Bundle();
			bundle.putLong("academic_id", academic_id);
			holidaylist.putExtras(bundle);
			startActivity(holidaylist);
			finish();

		} catch (Exception e) {

			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			holidaydatasource.close();
		}
	}

	//Show DatePicker Dialog
	@Override
	protected Dialog onCreateDialog(int id) {

		return new DatePickerDialog(this,
				DateSetListener,
				cYear, cMonth, cDay);

	}

	/**
	 * Get the selected date
	 */
	private DatePickerDialog.OnDateSetListener DateSetListener =
			new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear, 
				int selectedMonthOfYear, int selectedDayOfMonth) {
			Log.d(TAG, "Set Date");
			
			String date = new String("Date Selected "
					+selectedDayOfMonth+"/"+
					selectedMonthOfYear+"/"+
					selectedYear);
			Log.d(TAG, date);

			switch(typeIndicator){

			case ONE_DATE:
				Log.d(TAG, "setting oneDate...");
				mOneDate.clear();
				mOneDate.set(selectedYear, selectedMonthOfYear, selectedDayOfMonth);
				Log.d(TAG, mOneDate.toString());
				changeButtonText(ONE_DATE);
				break;

			case DURATIONSTART:
				Log.d(TAG, "setting startDate...");
				mStartDate.clear();
				mStartDate.set(selectedYear, selectedMonthOfYear, selectedDayOfMonth);
				Log.d(TAG, mStartDate.toString());
				changeButtonText(DURATIONSTART);
				break;

			case DURATIONEND:
				Log.d(TAG, "setting endDate...");
				mEndDate.clear();
				mEndDate.set(selectedYear, selectedMonthOfYear, selectedDayOfMonth);
				Log.d(TAG, mEndDate.toString());
				changeButtonText(DURATIONEND);
				break;
			}


		}
	};

	/**
	 * Get the selected date
	 * and set the text into related button
	 */
	public void changeButtonText(int DIALOG_ID){

		Log.d(TAG, "Change Button Text Here");
		switch(DIALOG_ID){

		case ONE_DATE:
			year = mOneDate.get(Calendar.YEAR);
			month = mOneDate.get(Calendar.MONTH);
			day = mOneDate.get(Calendar.DAY_OF_MONTH);
			break;

		case DURATIONSTART:
			year = mStartDate.get(Calendar.YEAR);
			month = mStartDate.get(Calendar.MONTH);
			day = mStartDate.get(Calendar.DAY_OF_MONTH);
			break;

		case DURATIONEND:
			year = mEndDate.get(Calendar.YEAR);
			month = mEndDate.get(Calendar.MONTH);
			day = mEndDate.get(Calendar.DAY_OF_MONTH);
			break;

		}

		// Month is 0 based, just add 1
		final StringBuilder newString =
				new StringBuilder()
		.append(day).append("/")
		.append(month + 1).append("/")
		.append(year);
		Log.d(TAG, newString.toString());


		switch(DIALOG_ID){

		case ONE_DATE:
			HolidayActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					btnOneDate.setText(newString);
				}
			});
			break;

		case DURATIONSTART:
			HolidayActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					btnStartDate.setText(newString);
				}
			});

			break;
		case DURATIONEND:
			HolidayActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					btnEndDate.setText(newString);
				}
			});
			break;
		}
	}

}
