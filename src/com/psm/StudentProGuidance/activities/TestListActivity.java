/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.adapters.TestAdapter;
import com.psm.StudentProGuidance.controllers.TestDataSource;
import com.psm.StudentProGuidance.entities.Test;

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
 * - show the layout ("testlist")
 * - show the list of test regards the semester
 * - enable create new test n also edit it
 *
 * Activity FROM
 * - MainMenuActivity
 * 
 * Activity TO
 * - SubjectsActivity
 * - TestForAddActivity
 * - TestForUpdateActivity (through TestAdapter)
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class TestListActivity extends Activity {

	private static final String TAG = "TEST LIST ACTIVITY";
	private ImageButton btnAdd;
	private ListView list;
	private TestDataSource testdatasource;

	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.testlist);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd_TL);
		list = (ListView) findViewById(R.id.list_TL);

		btnAdd.setOnClickListener(new ButtonOnClickListener());

		//access database
		testdatasource = new TestDataSource(this);

		/***
		 * GET User ID From MainMenuActivity
		 */
		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");
		
		setRowInList();

	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case R.id.btnAdd_TL :
				Intent intent = new Intent("android.intent.action.TESTADD");
				Bundle to = new Bundle();
				to.putLong("academic_id", academic_id);
				intent.putExtras(to);
				startActivity(intent);
				finish();
				break;

			}
		}
	}

	public void setRowInList(){
		if(academic_id>0){

			testdatasource.open();

			//search if existing record regarding academic id
			try {
				List<Test> dataList = new ArrayList<Test>();
				dataList = testdatasource.getAllTest(academic_id);
				testdatasource.close();

				if(dataList!=null){

					TestAdapter adapter = new TestAdapter(this,dataList);
					list.setAdapter(adapter);
				}
			} catch (Exception e) {

				Log.e(TAG, ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				testdatasource.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
	
		super.onRestart();
		setRowInList();
	}
	

}
