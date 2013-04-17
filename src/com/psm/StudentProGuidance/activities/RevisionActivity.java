/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.adapters.RevisionAdapter;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.controllers.TestDataSource;
import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.entities.Test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * This class will 
 * - show the layout ("revision")
 * - show the list of test regards the subjects selected
 *
 * Activity FROM
 * - MainMenuActivity
 * 
 * Activity TO
 * - N/A
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class RevisionActivity extends Activity {

	private static final String TAG = "REVISION ACTIVITY";
	private Spinner spinSubjects;
	private ListView list;
	private TestDataSource testdatasource;
	private SubjectsDataSource subdatasource;
	private List<Subjects> listSubjects;
	private Context thisContext = this;

	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.revision);

		spinSubjects = (Spinner) findViewById(R.id.spinSubjects_R);
		list = (ListView) findViewById(R.id.list_R);

		//access database
		testdatasource = new TestDataSource(this);
		subdatasource = new SubjectsDataSource(this);

		/***
		 * GET User ID From MainMenuActivity
		 */
		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");

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
						String value = listSubjects.get(index).getName();
						itemspin.add(key+"-"+value);

					}

					final List<String> copyOfSpin = itemspin;

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,copyOfSpin);

					spinSubjects.setAdapter(adapter);
					Log.d(TAG,"setAdapter pass");

					spinSubjects.setOnItemSelectedListener(new OnItemSelectedListener(){

						public void onItemSelected(AdapterView<?> adapter,
								View view, int position, long id) {

							findRelatedTest(position);

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


	public void findRelatedTest(int position){
		
		Log.d(TAG, "findRelatedTest");

		Subjects thisSubjects = listSubjects.get(position);
		testdatasource.open();
		try {
			List<Test> testList = testdatasource.getAllTest(thisSubjects);
			testdatasource.close();
			Log.d(TAG, "testList size"+testList.size());
			/*int size = testList.size();
			for(int index = 0;index<size;index++){
				Bonquet bonquet = new Bonquet();
				bonquet.setSubjects(thisSubjects);
				bonquet.setTest(testList.get(index));
				listBonquet.add(bonquet);
			}*/
			 
			RevisionAdapter adapter = 
					new RevisionAdapter(thisContext,testList);
			list.setAdapter(adapter);
			Log.d(TAG, "Adapter set");
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
			Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			testdatasource.close();
		}



	}

}
