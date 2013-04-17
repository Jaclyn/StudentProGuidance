package com.psm.StudentProGuidance.activities;

import java.util.List;

import com.psm.StudentProGuidance.adapters.SubjectsAdapter;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Subjects;

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
 * - show layout ("subjectslist")
 * - connect to database and access subjects details
 * - call out SubjectsAdapter and display it in list view
 * - allow access to insert new record of Subjects or delete it
 * 
 * Activity FROM
 * - MainMenuActivity
 * 
 * Activity TO
 * - SubjectsForAddActivity
 * - SubjectsForUpdateActivity(through SubjectsAdapter)
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class SubjectsListActivity extends Activity{

	private ImageButton btnAdd;
	static final String DEFAULT = "-";
	public static final String TAG = "SUBJECTS LIST ACTIVITY";
	private ListView listView;
	private SubjectsDataSource subdatasource;
	private List<Subjects> dataList;
	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subjectslist);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd_SL);
		listView = (ListView) findViewById(R.id.list_SL);

		btnAdd.setOnClickListener(new ButtonOnClickListener());

		subdatasource = new SubjectsDataSource(this);
		
		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");
	
		Log.d(TAG, "academic_id : "+academic_id);

		setRowsInList();
	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case  R.id.btnAdd_SL :
				Intent subjectPage = new Intent("android.intent.action.SUBJECTSADD");
				Bundle bundle = new Bundle();
				bundle.putLong("academic_id", academic_id);
				subjectPage.putExtras(bundle);
				startActivity(subjectPage);
				finish();
				break;

			}
		}
	}
	
	/**
	 * This method select all the related records in database
	 * and set them into list view
	 */
	public void setRowsInList(){
		/**
		 * Load Subjects records regarding academic id
		 */
		if(academic_id>0){
			Log.d(TAG, "Academic id verify!");
			//search if existing record regarding academic id
			try {

				subdatasource.open();
				Log.d(TAG, "Open Subjects Data Source");

				dataList = subdatasource.getAllSubjects(academic_id);
				Log.d(TAG, "dataList : "+dataList);
				subdatasource.close();

				if(dataList!=null){
					SubjectsAdapter adapter = new SubjectsAdapter(this,dataList);
					listView.setAdapter(adapter);
				}


			} catch (Exception e) {

				Log.e(TAG, ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				subdatasource.close();
			}
		}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setRowsInList();
		
	}


}
