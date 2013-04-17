package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.adapters.ResultsAdapter;
import com.psm.StudentProGuidance.controllers.ResultsDataSource;
import com.psm.StudentProGuidance.entities.Results;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class handles
 * - show layout ("results_subjects")
 * - show the list of results of the subjects
 * 
 * Activity FROM
 * - ResultsActivity
 * 
 * Activity BACK
 * - ResultsActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * - subjects_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class ResultsListActivity extends Activity {

	private ListView listView;
	private TextView lblSubjects;
	private ResultsDataSource resdatasource;
	private List<Results> resultsList = new ArrayList<Results>();
	private static final String TAG = "SUBJECTS RESULTS ACTIVITY";
	private long academic_id;
	private long subjects_id;
	private String subjects_name;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results_subjects);

		lblSubjects = (TextView) findViewById(R.id.lblSubjects_RES);
		listView = (ListView) findViewById(R.id.list_RES);
		
		resdatasource = new ResultsDataSource(this);

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");
		subjects_id = from.getLong("subjects_id");
		subjects_name = from.getString("subjects_name");
		
		if(academic_id>0&&subjects_id>0){
			
			lblSubjects.setText(subjects_name);
			resdatasource.open();
			try {
				Results results = new Results();
				results.setAcademic_id(academic_id);
				results.setSubjects_id(subjects_id);
				resultsList = resdatasource.getAllResults(results);
				if(resultsList!=null){
					ResultsAdapter adapter = 
							new ResultsAdapter(this,resultsList);
					listView.setAdapter(adapter);
					
				}
				
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage());
				
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
			}finally{
				resdatasource.close();
			}
		}
	

	}

	
}
