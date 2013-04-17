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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
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
 * This class handles 
 * - show layout ("academic")
 * - link to database and access user academic details
 * - insert new record of academic into database
 * - create record when
 *    ->go in HolidayListActivity
 *    ->button OK is clicked
 * - delete both academic and holiday records 
 *   while activity stop but
 *   button OK is not clicked
 * 
 * Activity FROM
 * - SkipLoginActivity
 * - UserChooseSemActivity
 * 
 * Activity TO
 * - HolidayListActivity
 * - UserChooseSemActivity
 * 
 * Bundle IN (key)
 * - name
 * - userid
 * 
 * Bundle OUT (key)
 * - name
 * - userid
 * - academic_id
 * 
 * @author  Ng Xin Man
 */
public class AcademicForAddActivity extends Activity {

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
	public static final String TAG = "ACADEMIC ADD ACTIVITY";
	private Calendar startSemDate = Calendar.getInstance();
	private Calendar endSemDate = Calendar.getInstance();
	private int cYear,cMonth,cDay;
	private int ID_INUSE;
	private int year, month, day;
	private AcademicDataSource datasource;
	private HolidayDataSource holidatasource;
	private String userID;
	private String name;
	private long referAcademicId;
	private boolean click;
	private Context context;
	private List<Integer> dayValueList;
	private ChooseDayAdapter adapter;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.academic);	
		context=this;

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

		btnChoose.setOnClickListener(new ButtonOnClickListener());
		btnAddHoliday.setOnClickListener(new ButtonOnClickListener());
		btnSemStart.setOnClickListener(new ButtonOnClickListener());
		btnSemEnd.setOnClickListener(new ButtonOnClickListener());
		btnSend.setOnClickListener(new ButtonOnClickListener());

		datasource = new AcademicDataSource(this);
		holidatasource = new HolidayDataSource(this);

		dayValueList = new ArrayList<Integer>();
		for(int initial=2;initial<7;initial++)
			dayValueList.add(new Integer(initial));

		//Toast.makeText(context, String.valueOf(dayValueList.size()), Toast.LENGTH_SHORT).show();
		click=false;
		referAcademicId=0;

		Bundle extras = getIntent().getExtras();
		userID = extras.getString("userid");
		name = extras.getString("name");
		Log.d(TAG,"userID : "+userID);
		Log.d(TAG,"name : "+name);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();

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
				click = true;
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
	 * - validate input fields
	 * - create new Academic record
	 * - get the current Academic id and put in bundle
	 */
	public void doubleCheck(){

		Log.d(TAG,"doubleCheck() here");

		//validation of input fields
		if(txtSem.getText().toString().equals("")
				||btnSemStart.getText().toString().equals(getString(R.string.txtStart))
				||btnSemEnd.getText().toString().equals(getString(R.string.txtEnd))){

			Toast toast = Toast.makeText(this, "Please Verify Your Input Fields Before Proceed.", 
					Toast.LENGTH_SHORT);
			toast.show();

		}else{

			//Create new Academic profile
			Academic newAcademic = new Academic();
			newAcademic.setSemester(new Integer(txtSem.getText().toString()));
			String aca_startDate = btnSemStart.getText().toString();
			newAcademic.setStartdate(aca_startDate);
			String aca_endDate = btnSemEnd.getText().toString();
			newAcademic.setEnddate(aca_endDate);
			newAcademic.setUser_id(userID);
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
			newAcademic.setDaySet(daySet);

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
				Toast toast = Toast.makeText(this, "Please Verify The Date Input.", Toast.LENGTH_SHORT);
				toast.show();
			}else{

				try {

					datasource.open();
					if(datasource.verifySemester(newAcademic)==false){
						if(referAcademicId != 0){
							newAcademic.setId(referAcademicId);
							datasource.updateAcademic(newAcademic);
						}else{
							referAcademicId=datasource.createAcademic(newAcademic);	
						}			

						Bundle bundle = new Bundle();
						//get the current record's academic id then put into bundle
						bundle.putLong("academic_id", referAcademicId);
						bundle.putString("name", name);
						bundle.putString("userid",userID);

						//go to HolidayActivity
						datasource.close();

						if(ID_INUSE == GO_HOLIDAY){
							Intent holidaylist = new Intent("android.intent.action.HOLIDAYLIST");
							holidaylist.putExtras(bundle);
							startActivity(holidaylist);
						}else{
							Intent userchoice = new Intent("android.intent.action.USERSEM");
							userchoice.putExtras(bundle);
							startActivity(userchoice);
							finish();
						}
					}else{

						Toast toast = Toast.makeText(this, "Invalid. Semester profile exists.", 
								Toast.LENGTH_SHORT);
						toast.show();
					}

				} catch (Exception e) {

					Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();

					datasource.close();
				}

			}
		}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 * 
	 * This event method validate the records 
	 * that already created in database.
	 * Record have to be deleted if 
	 * record is created without clicking the
	 * button add.
	 */
	@Override
	protected void onDestroy() {

		if(referAcademicId > 0 && click == false){

			try {
				holidatasource.open();
				holidatasource.deleteHolidayWithAcademicId(referAcademicId);
				holidatasource.close();

				datasource.open();
				datasource.deleteAcademicById(referAcademicId);
				datasource.close();

			} catch (Exception e) {
				Log.e(TAG, ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

			}			
		}
		super.onDestroy();			
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
			AcademicForAddActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					btnSemStart.setText(newString);
				}
			});
			break;
		case ENDSEM_DATE_DIALOG_ID:
			AcademicForAddActivity.this.runOnUiThread(new Runnable() {
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
