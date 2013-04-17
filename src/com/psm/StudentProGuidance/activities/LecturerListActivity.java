package com.psm.StudentProGuidance.activities;

import java.util.List;

import com.psm.StudentProGuidance.adapters.LecturerAdapter;
import com.psm.StudentProGuidance.controllers.LecturerDataSource;
import com.psm.StudentProGuidance.entities.Lecturer;

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
 * - show layout ("lecturerlist")
 * - connect to database and access lecturer details
 * - call out LecturerAdapter and display it in list view
 * - allow access to insert new record of lecturer or delete it
 * 
 * Activity FROM
 * - MainMenuActivity
 * 
 * Activity TO
 * - LecturerActivity
 * 
 * Activity BACK
 * - MainMenuActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class LecturerListActivity extends Activity{

	private ImageButton btnAdd;
	public static final String TAG = "LECTURER LIST ACTIVITY";
	private ListView list;
	private LecturerDataSource lecdatasource;
	private List<Lecturer> dataList;
	private long academic_id;
	private int semester;

	/* (non-Javadoc)
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lecturerlist);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd_LL);
		list = (ListView) findViewById(R.id.list_LL);

		btnAdd.setOnClickListener(new ButtonOnClickListener());

		lecdatasource = new LecturerDataSource(this);

		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");
	
		Log.d(TAG, "academic_id : "+academic_id);
		
		setRowIntoList();
	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case  R.id.btnAdd_LL :
				Intent lecturer = new Intent("android.intent.action.LECTURERADD");
				Bundle bundle = new Bundle();
				bundle.putLong("academic_id", academic_id);
				bundle.putInt("semester", semester);
				lecturer.putExtras(bundle);
				startActivity(lecturer);
				break;

			}
		}
	}
	
	public void setRowIntoList(){
		/**
		 * Load holiday records regarding academic id
		 */
		if(academic_id>0){
			Log.d(TAG, "Academic id verify!");
			//search if existing record regarding academic id
			try {

				lecdatasource.open();
				Log.d(TAG, "Open Lecturer Data Source");

				dataList = lecdatasource.getAllLecturer(academic_id);
				Log.d(TAG, "dataList : "+dataList);
				lecdatasource.close();

				if(dataList!=null){
					LecturerAdapter adapter = new LecturerAdapter
							(this,dataList);
					list.setAdapter(adapter);
				}


			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				lecdatasource.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setRowIntoList();
	}


}
