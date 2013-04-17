/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.adapters.ClassAdapter;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.entities.Class;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class will 
 * - show the layout ("classlist")
 * - show the list of class regards the semester
 *
 * Activity FROM
 * - TimeTableActivity
 * 
 * Activity TO
 * - ClassForAddActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class ClassListActivity extends Activity {

	//private static final String TAG = "CLASS LIST ACTIVITY";
	private ImageButton btnAdd;
	private ListView list;
	private ClassDataSource cladatasource;

	private long academic_id;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.classlist);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd_CLAL);
		list = (ListView) findViewById(R.id.list_CLAL);

		btnAdd.setOnClickListener(new ButtonOnClickListener());

		//access database
		cladatasource = new ClassDataSource(this);
		
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

			case R.id.btnAdd_CLAL :
				Intent intent = new Intent("android.intent.action.CLASSADD");
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

			cladatasource.open();

			//search if existing record regarding academic id
			try {
				List<Class> dataList = new ArrayList<Class>();
				dataList = cladatasource.getAllClass(academic_id);
				cladatasource.close();

				if(dataList!=null){
				

					ClassAdapter adapter = new ClassAdapter(this,dataList);
					list.setAdapter(adapter);
				}
			} catch (Exception e) {


				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				cladatasource.close();
				
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
