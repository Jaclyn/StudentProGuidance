/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.psm.StudentProGuidance.adapters.ChooseDayAdapter;
import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.HolidayDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Holiday;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class will 
 * - show layout ("academic")
 * - link to database and access user academic details
 * - update existing academic record in database
 * - hold the existing holiday record and
 *   delete the records in database and 
 *   re-insert the old records if the 
 *   confirm update is not chosen
 * 
 * Activity FROM
 * - MainMenuActivity
 * 
 * Activity TO
 * - HolidayListActivity
 * 
 * Activity BACK
 * - MainMenuActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author  Ng Xin Man
 *
 */
public class AcademicForUpdateActivity extends Activity {

	private EditText txtSem;
	private Button btnSemStart;
	private Button btnSemEnd;
	private ImageButton btnAddHoliday;
	private Button btnSend;
	private Button btnChoose;

	static final int STARTSEM_DATE_DIALOG_ID = 0;
	static final int ENDSEM_DATE_DIALOG_ID = 1;
	static final int HOLIDAY_DIALOG_ID = 2;
	static final int GO_HOLIDAY = 3;
	static final int GO_MENU = 4;
	public static final String TAG = "ACADEMIC UPDATE ACTIVITY";
	private Calendar startSemDate = Calendar.getInstance();
	private Calendar endSemDate = Calendar.getInstance();
	private int cYear,cMonth,cDay;
	private int ID_INUSE;
	private int year, month, day;
	private AcademicDataSource datasource;
	private HolidayDataSource holidatasource;
	private Academic thisAcademic;
	private long referAcademicId;
	private boolean click;
	private List<Holiday> existingRecord;
	private Context context;
	private List<Integer> dayValueList;
	private ChooseDayAdapter adapter;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * Existing user will use back the old record,
	 * otherwise the userid will be inserted into others details 
	 * fist before the real academic id is inserted.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.academic);	
		context =this;

		btnSemStart = (Button) findViewById(R.id.btnSemStartDate_A);
		btnSemEnd = (Button) findViewById(R.id.btnSemEndDate_A);
		txtSem = (EditText) findViewById(R.id.txtSemester_A);
		btnChoose = (Button) findViewById(R.id.btnChoose_A);
		btnAddHoliday = (ImageButton) findViewById(R.id.btnAddHoliday_A);
		btnSend = (Button) findViewById(R.id.btnSubmit_A);

		//Get current System date
		final Calendar c = Calendar.getInstance();
		cYear = c.get(Calendar.YEAR);
		cMonth = c.get(Calendar.MONTH);
		cDay = c.get(Calendar.DAY_OF_MONTH);

		dayValueList = new ArrayList<Integer>();
		for(int initial=2;initial<7;initial++)
			dayValueList.add(new Integer(initial));
		click = false;

		btnChoose.setOnClickListener(new ButtonOnClickListener());
		btnAddHoliday.setOnClickListener(new ButtonOnClickListener());
		btnSemStart.setOnClickListener(new ButtonOnClickListener());
		btnSemEnd.setOnClickListener(new ButtonOnClickListener());
		btnSend.setOnClickListener(new ButtonOnClickListener());

		Bundle extras = getIntent().getExtras();
		referAcademicId = extras.getLong("academic_id");

		Log.d(TAG,"referAcademicId = "+referAcademicId);

		datasource = new AcademicDataSource(this);
		holidatasource = new HolidayDataSource(this);

		/**
		 * Load the selected semester details
		 */
		try {

			datasource.open();		
			thisAcademic = datasource.getAcademicByID(referAcademicId);	
			datasource.close();

			Log.d(TAG,"thisAcademic = "+thisAcademic);
			/***
			 * If the record of the user is found.
			 * Load it.
			 **/
			if( thisAcademic != null){

				txtSem.setText(""+thisAcademic.getSemester());
				btnSemStart.setText(""+thisAcademic.getStartdate());
				btnSemEnd.setText(""+thisAcademic.getEnddate());
				String[] splitDays = thisAcademic.getDaySet().split(",");
				dayValueList.clear();
				for(int i=0;i<splitDays.length;i++)
					dayValueList.add(Integer.parseInt(splitDays[i]));
			}

			/**
			 * Get the existing records of holidays
			 */
			holidatasource.open();
			existingRecord =  holidatasource.getAllHolidays(referAcademicId);
			holidatasource.close();

		} catch (Exception e) {

			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();

			datasource.close();

		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 * 
	 * This event method validate the records 
	 * that already created in holiday table.
	 * Record have to be deleted if 
	 * record is updated without 
	 * the agreement to update
	 */
	@Override
	protected void onDestroy() {
		if(click == false){

			try {
				holidatasource.open();
				holidatasource.deleteHolidayWithAcademicId(referAcademicId);

				for(int index = 0; index < existingRecord.size(); index++){
					holidatasource.createHoliday(existingRecord.get(index));
				}
				holidatasource.close();

			} catch (Exception e) {
				Log.e(TAG, ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

			}			
		}
		super.onDestroy();			
	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnChoose_A:
				showDialogChooseDay();
				break;

			case R.id.btnSemStartDate_A :
				ID_INUSE = STARTSEM_DATE_DIALOG_ID;
				showDialog(STARTSEM_DATE_DIALOG_ID);
				break;

			case R.id.btnSemEndDate_A :
				ID_INUSE = ENDSEM_DATE_DIALOG_ID;
				showDialog(ENDSEM_DATE_DIALOG_ID);
				break;

			case  R.id.btnAddHoliday_A :
				ID_INUSE = GO_HOLIDAY;
				doubleCheck();
				break;
			case  R.id.btnSubmit_A :
				ID_INUSE = GO_MENU;
				doubleCheck();
				break;

			}
		}
	}


	/**
	 * This method 
	 * - display the dialog for user to choose
	 * Day involved of class
	 */
	public void showDialogChooseDay(){
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.chooseday);
		dialog.setTitle("Days Of Class");

		ListView listView = (ListView)dialog.findViewById(R.id.listview_CD);
		adapter = new ChooseDayAdapter(this,dayValueList);
		listView.setAdapter(adapter);

		dialog.setOnDismissListener(new OnDismissListener(){

			public void onDismiss(DialogInterface arg0) {
				dayValueList = adapter.getCheckedItems();
				/*Toast.makeText(context, 
						String.valueOf(dayValueList.size()), 
						Toast.LENGTH_SHORT).show();
				 */			}

		});
		dialog.show();

	}
	/**
	 * This method will 
	 * - validate all the input fields
	 * - ask for confirmation for update
	 * - go for holiday page
	 */
	public void doubleCheck(){

		Log.d(TAG,"doubleCheck() here");

		//validation of input fields
		if(txtSem.getText().toString().equals("")
				||btnSemStart.getText().toString().equals("")
				||btnSemEnd.getText().toString().equals("")){

			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();


		}else{

			//CHECK CURRENT ACADEMIC IS IN DATABASE OR NOT
			//IF RECORD FOUND
			if(referAcademicId > 0){

				try {

					Bundle bundle = new Bundle();
					//get the current record's academic id then put into bundle
					bundle.putLong("academic_id", referAcademicId);

					if(ID_INUSE == GO_HOLIDAY){
						Intent holidaylist = new Intent("android.intent.action.HOLIDAYLIST");
						holidaylist.putExtras(bundle);
						startActivity(holidaylist);
					}
					//Call for confirmation for update
					else{
						AlertDialog alert = new AlertDialog.Builder(this).create();
						alert.setTitle("Confirmation");
						alert.setMessage("Confirm Update?");
						alert.setButton("Yes", new DialogInterface.OnClickListener() {  
							public void onClick(DialogInterface dialog, int which) { 
								click = true;
								UpdateProgress();
								return;
							} });   
						alert.setButton2("No", new DialogInterface.OnClickListener() {  
							public void onClick(DialogInterface dialog, int which) {  
								return;  
							} });  
						alert.show();
					}

					//finish();

				} catch (Exception e) {

					Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();

					datasource.close();
				}			

			}
		}
	}

	/**
	 * Handle update process for academic records
	 */
	public void UpdateProgress(){

		//Create new Academic profile
		Academic academic = new Academic();
		academic.setSemester(new Integer(txtSem.getText().toString()));
		String aca_startDate = btnSemStart.getText().toString();
		academic.setStartdate(aca_startDate);
		String aca_endDate = btnSemEnd.getText().toString();
		academic.setEnddate(aca_endDate);
		academic.setId(referAcademicId);
		
		String daySet = new String();
		Iterator<Integer> dayIter = dayValueList.iterator();
		int first=0;
		while(dayIter.hasNext()){
			if(first==0){
				daySet = String.valueOf(dayIter.next());
				first=1;
			}else{
				daySet = daySet + "," +String.valueOf(dayIter.next());
			}
		}
		academic.setDaySet(daySet);

		Calendar cStart = Calendar.getInstance();
		String[]startDate = aca_startDate.split("/");
		int startDay  = Integer.parseInt(startDate[0])+1;
		int startMonth  = Integer.parseInt(startDate[1]);
		int startYear  = Integer.parseInt(startDate[2]);
		cStart.set(startYear, startMonth, startDay);

		Calendar cEnd = Calendar.getInstance();
		String[]endDate = aca_endDate.split("/");
		int endDay  = Integer.parseInt(endDate[0]);
		int endMonth  = Integer.parseInt(endDate[1]);
		int endYear  = Integer.parseInt(endDate[2]);
		cEnd.set(endYear, endMonth, endDay);

		if(cEnd.before(cStart)){

			Toast toast = Toast.makeText(
					this, "Please Verify The Date Input.", Toast.LENGTH_SHORT);
			toast.show();

		}else{

			datasource.open();
			try {

				datasource.updateAcademic(academic);
				datasource.close();

				Toast toast = Toast.makeText(this, "Successfully Update!", 
						Toast.LENGTH_SHORT);
				toast.show();


			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				datasource.close();
			}
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
	 * Change Button Text after select the date
	 * @param DIALOG_ID
	 */
	public void changeButtonText(int DIALOG_ID){

		Log.d(TAG, "Change Button Text Here");
		switch(DIALOG_ID){

		case STARTSEM_DATE_DIALOG_ID:
			year = startSemDate.get(Calendar.YEAR);
			month = startSemDate.get(Calendar.MONTH);
			day = startSemDate.get(Calendar.DAY_OF_MONTH);
			break;

		case ENDSEM_DATE_DIALOG_ID:
			year = endSemDate.get(Calendar.YEAR);
			month = endSemDate.get(Calendar.MONTH);
			day = endSemDate.get(Calendar.DAY_OF_MONTH);
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
		case STARTSEM_DATE_DIALOG_ID:
			AcademicForUpdateActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					btnSemStart.setText(newString);
				}
			});
			break;
		case ENDSEM_DATE_DIALOG_ID:
			AcademicForUpdateActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					btnSemEnd.setText(newString);
				}
			});
			break;
		}
	}

	/**
	 * Get the current selected date and 
	 * invokes changeButtonText() to display the 
	 * selected date.
	 */
	private DatePickerDialog.OnDateSetListener DateSetListener =
			new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear, 
				int selectedMonthOfYear, int selectedDayOfMonth) {
			Log.d(TAG, "Set Date");

			String date = new String("Date Selected "
					+selectedMonthOfYear+"/"+
					selectedDayOfMonth+"/"+
					selectedYear);
			Log.d(TAG, date);

			switch(ID_INUSE){

			case STARTSEM_DATE_DIALOG_ID:
				Log.d(TAG, "setting startSemDat...");
				startSemDate.clear();
				startSemDate.set(selectedYear, selectedMonthOfYear, selectedDayOfMonth);
				Log.d(TAG, startSemDate.toString());
				changeButtonText(STARTSEM_DATE_DIALOG_ID);
				break;

			case ENDSEM_DATE_DIALOG_ID:
				Log.d(TAG, "setting startSemDate...");
				endSemDate.clear();
				endSemDate.set(selectedYear, selectedMonthOfYear, selectedDayOfMonth);
				Log.d(TAG, endSemDate.toString());
				changeButtonText(ENDSEM_DATE_DIALOG_ID);
				break;
			}
		}
	};

}
