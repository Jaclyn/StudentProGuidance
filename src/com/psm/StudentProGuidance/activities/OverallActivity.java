package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.adapters.OverallAdapter;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Subjects;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class handles
 * - show layout ("overall")
 * - show the list of results of the semester
 * 
 * Activity FROM
 * - ResultsActivity
 * 
 * Activity BACK
 * - ResultsActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class OverallActivity extends Activity {

	private ListView listView;
	private SubjectsDataSource subdatasource;
	private List<Subjects> subjectsList = new ArrayList<Subjects>();
	private static final String TAG = "OVERALL ACTIVITY";
	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overall);

		listView = (ListView) findViewById(R.id.list_O);
		
		subdatasource = new SubjectsDataSource(this);

		Bundle fromLogin = getIntent().getExtras();
		academic_id = fromLogin.getLong("academic_id");
		
		if(academic_id>0){
			subdatasource.open();
			try {
				subjectsList = subdatasource.getAllSubjects(academic_id);
				if(subjectsList!=null){
					OverallAdapter adapter = 
							new OverallAdapter(this,subjectsList);
					listView.setAdapter(adapter);
					
				}
				
			} catch (Exception e) {
				Log.e(TAG, ""+e.getLocalizedMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				
			}finally{
				subdatasource.close();
			}
		}
	

	}

	
}
