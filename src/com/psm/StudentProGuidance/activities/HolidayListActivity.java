package com.psm.StudentProGuidance.activities;

import java.util.List;

import com.psm.StudentProGuidance.adapters.HolidayAdapter;
import com.psm.StudentProGuidance.controllers.HolidayDataSource;
import com.psm.StudentProGuidance.entities.Holiday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class will 
 * - show layout ("holidaylist")
 * - connect to database and access holiday details
 * - call out HolidayAdapter and display it in list view
 * - allow access to insert new record of Holiday or delete it
 * 
 * Activity FROM
 * - AcademicForAddActivity
 * - AcademicForUpdateActivity
 * 
 * Activity BACK
 * - AcademicForAddActivity
 * - AcademicForUpdateActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class HolidayListActivity extends Activity{

	private ImageButton btnAdd;
	static final int ONE_DATE = 0;
	static final int DURATIONSTART = 1;
	static final int DURATIONEND = 2;
	static final int ADD = 3;
	static final int FINISH = 4;
	static final String DEFAULT = "-";
	public static final String TAG = "HOLIDAY LIST ACTIVITY";
	public static final String ADD_ACADEMIC = "add";
	public static final String UPDATE_ACADEMIC = "update";
	private ListView holidayView;
	private HolidayDataSource holidaydatasource;
	private List<Holiday> dataList;
	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.holidaylist);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd_HL);
		holidayView = (ListView) findViewById(R.id.list_HL);

		btnAdd.setOnClickListener(new ButtonOnClickListener());

		holidaydatasource = new HolidayDataSource(this);
		
		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");
	
		Log.d(TAG, "academic_id : "+academic_id);

		/**
		 * Load holiday records regarding academic id
		 */
		if(academic_id>0){
			Log.d(TAG, "Academic id verify!");
			//search if existing record regarding academic id
			try {

				holidaydatasource.open();
				Log.d(TAG, "Open HolidayData Source");

				dataList = holidaydatasource.getAllHolidays(academic_id);
				Log.d(TAG, "dataList : "+dataList);
				holidaydatasource.close();

				if(dataList!=null){
					HolidayAdapter adapter = new HolidayAdapter(this,dataList);
					holidayView.setAdapter(adapter);
				}


			} catch (Exception e) {

				Log.e(TAG, ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				holidaydatasource.close();
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

			case  R.id.btnAdd_HL :
				Intent holiday = new Intent("android.intent.action.HOLIDAY");
				Bundle bundle = new Bundle();
				bundle.putLong("academic_id", academic_id);
				holiday.putExtras(bundle);
				startActivity(holiday);
				finish();
				break;

			}
		}
	}


}
