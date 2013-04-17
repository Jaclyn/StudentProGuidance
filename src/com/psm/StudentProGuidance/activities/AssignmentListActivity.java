/**
 * 
 */
package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.psm.StudentProGuidance.adapters.AssignmentAdapter;
import com.psm.StudentProGuidance.controllers.AssignmentDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Assignment;
import com.psm.StudentProGuidance.entities.Subjects;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * This class will 
 * - show the layout ("assignmentlist")
 * - show the list of assignments regards the semester
 * - enable create new assignments n also edit it
 *
 * Activity FROM
 * - MainMenuActivity
 * 
 * Activity TO
 * - SubjectsActivity
 * - AssignmentsActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - academic_id
 * 
 * @author Ng Xin Man
 */
public class AssignmentListActivity extends Activity {

	//private static final String TAG = "ASSIGNMENT LIST ACTIVITY";
	private ImageButton btnAdd;
	private ListView list;
	private AutoCompleteTextView txtSearch;
	private List<Subjects> listSubjects;
	private AssignmentDataSource assdatasource;
	private SubjectsDataSource subdatasource;
	private RadioGroup radioGroupSearch;
	private Context thisContext;
	private int mYear;
	private int mMonth;
	private int mDay;
	private long academic_id;
	private static int type=1;
	private List<String> itemspin;
	private List<Assignment> dataList;
	private TextWatcherListener textWatcher;
	private List<String> noFoundMsg;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.assignmentlist);
		thisContext = this;

		btnAdd = (ImageButton) findViewById(R.id.btnAdd_ASL);
		list = (ListView) findViewById(R.id.list_ASL);
		txtSearch = (AutoCompleteTextView) findViewById(R.id.txtSearch_S);
		radioGroupSearch = (RadioGroup) findViewById(R.id.rgSearch_S);

		textWatcher = new TextWatcherListener();

		btnAdd.setOnClickListener(new ButtonOnClickListener());
		radioGroupSearch.setOnCheckedChangeListener(new CheckedChangeListener());

		//access database
		assdatasource = new AssignmentDataSource(this);
		subdatasource = new SubjectsDataSource(this);

		itemspin = new ArrayList<String>();
		dataList = new ArrayList<Assignment>();

		noFoundMsg = new ArrayList<String>();
		noFoundMsg.clear();
		noFoundMsg.add("\tNo Data Found");

		//Get current System date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		/***
		 * GET User ID From MainMenuActivity
		 */
		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");

		setRowInList();

	}


	private class CheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

		public void onCheckedChanged(RadioGroup radioGroup, int checked) {

			txtSearch.setText("");

			switch (checked) {
			case R.id.rdTitle_S : 
				type=1;
				//txtSearch.setFocusable(true);
				/*Toast.makeText(thisContext,"Focusable"+txtSearch.isFocusable(), 
						Toast.LENGTH_SHORT).show();*/
				txtSearch.addTextChangedListener(textWatcher);
				txtSearch.setOnTouchListener(null);
				break;

			case R.id.rdSubjects_S :
				type=2;
				setListIntoSpin();
				//txtSearch.setFocusable(false);
				/*Toast.makeText(thisContext,"Focusable"+txtSearch.isFocusable(), 
						Toast.LENGTH_SHORT).show();*/
				if(txtSearch.getAdapter().getCount()>0)
					txtSearch.showDropDown();

				txtSearch.setOnTouchListener(new OnTouchListener(){

					public boolean onTouch(View view, MotionEvent event) {

						if(event.getAction()==MotionEvent.ACTION_DOWN){

							if(txtSearch.getAdapter().getCount()>0)
								txtSearch.showDropDown();
						}

						return false;
					}
				});

				break;

			case R.id.rdDate_S :
				type=3;
				//txtSearch.setFocusable(false);
				/*Toast.makeText(thisContext,"Focusable"+txtSearch.isFocusable(), 
						Toast.LENGTH_SHORT).show();*/
				new DatePickerDialog(thisContext,
						dateSetListener,
						mYear, mMonth, mDay).show();
				txtSearch.setOnTouchListener(new OnTouchListener(){

					public boolean onTouch(View view, MotionEvent event) {
						txtSearch.setOnKeyListener(null);
						if(event.getAction()==MotionEvent.ACTION_DOWN){

							new DatePickerDialog(thisContext,
									dateSetListener,
									mYear, mMonth, mDay).show();
						}

						return false;
					}

				});
				break;
			}
		}

	}
	/**
	 * @author User
	 *
	 */
	private class TextWatcherListener implements TextWatcher{

		public void afterTextChanged(Editable arg0) {
		
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
			if(type==1){
				
				showSuggestionByTitle(s.toString());
				if(txtSearch.getAdapter().getCount()>0)
					txtSearch.showDropDown();
				searchingResults(s.toString());
			
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

			case R.id.btnAdd_ASL :
				Intent intent = new Intent("android.intent.action.ASSIGNMENTADD");
				Bundle to = new Bundle();
				to.putLong("academic_id", academic_id);
				intent.putExtras(to);
				startActivity(intent);
				finish();

			}
		}
	}

	private DatePickerDialog.OnDateSetListener dateSetListener =
			new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear, 
				int selectedMonthOfYear, int selectedDayOfMonth) {

			// Month is 0 based, just add 1
			final StringBuilder newString =
					new StringBuilder()
			.append(selectedDayOfMonth).append("/")
			.append(selectedMonthOfYear + 1).append("/")
			.append(selectedYear);

			txtSearch.setText(newString);
			searchItemByDate(newString.toString());

		}
	};

	public void showSuggestionByTitle(final String text){
		if(academic_id>0){

			assdatasource.open();

			//search if existing record regarding academic id
			try {
				dataList.clear();
				dataList = assdatasource.showSuggestionByTitle(text);
				assdatasource.close();

				if(dataList!=null){
					txtSearch.setAdapter(null);

					AssignmentAdapter adapter = new AssignmentAdapter(this,dataList);
					txtSearch.setAdapter(adapter);

					txtSearch.setOnItemClickListener(new OnItemClickListener(){

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							txtSearch.setText(dataList.get(position).getTitle());
							searchingResults(text);
						}

					});
				}
			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				assdatasource.close();
			}
		}
	}


	public void searchingResults(String text){

		if(academic_id>0){

			assdatasource.open();

			//search if existing record regarding academic id
			try {
				List<Assignment> dataList = new ArrayList<Assignment>();
				dataList = assdatasource.searchAssignmentsByTitle(text);
				assdatasource.close();

				if(dataList!=null){
					AssignmentAdapter adapter = new AssignmentAdapter(this,dataList);
					list.setAdapter(adapter);
				}if(dataList.size()<1){

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,noFoundMsg);
					list.setAdapter(adapter);
				}
			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				assdatasource.close();
			}
		}
		//adapt.
	}

	public void searchingSubjectsByText(String text){

		if(academic_id>0){

			assdatasource.open();

			//search if existing record regarding academic id
			try {
				List<Assignment> dataList = new ArrayList<Assignment>();
				dataList = assdatasource.searchAssignmentsBySubjectsText(text);
				assdatasource.close();


				if(dataList!=null){
					AssignmentAdapter adapter = new AssignmentAdapter(this,dataList);
					list.setAdapter(adapter);
				}if(dataList.size()<1){

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,noFoundMsg);
					list.setAdapter(adapter);
				}
			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				assdatasource.close();
			}
		}
	}

	/**
	 * This method set all the subjects in database
	 * into spinner
	 */
	public void setListIntoSpin(){
		if(academic_id>0){

			try {
				subdatasource.open();
				listSubjects = subdatasource.getAllSubjects(academic_id);
				subdatasource.close();

				if(listSubjects!=null){
					txtSearch.setAdapter(null);

					int size = listSubjects.size();

					itemspin.clear();
					for(int index=0; index<size; index++){

						String key = listSubjects.get(index).getAbbrev();
						String value = listSubjects.get(index).getName();
						itemspin.add(key+" - "+value);
					}

					final List<String> copyOfSpin = itemspin;

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,copyOfSpin);

					txtSearch.setAdapter(adapter);
					//Log.d(TAG,"setAdapter pass");
					//txtSearch.showDropDown();

					txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> adapter, View view,
								int position, long id) {
							searchItemBySubjectsId(position);
						}

					});

				}

			} catch (Exception e) {

				Log.e(" - doInBackground ", ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				subdatasource.close();
				assdatasource.close();
			}
		}
	}

	public void setRowInList(){
		if(academic_id>0){

			assdatasource.open();

			//search if existing record regarding academic id
			try {
				List<Assignment> dataList = new ArrayList<Assignment>();
				dataList = assdatasource.getAllAssignment(academic_id);
				assdatasource.close();

				if(dataList!=null){

					AssignmentAdapter adapter = new AssignmentAdapter(this,dataList);
					list.setAdapter(adapter);
				}if(dataList.size()<1){

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,noFoundMsg);
					list.setAdapter(adapter);
				}
			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				assdatasource.close();
			}
		}
	}

	public void searchItemBySubjectsId(int position){
		if(academic_id>0){

			assdatasource.open();

			long selectedId= listSubjects.get(position).getId();
			//search if existing record regarding academic id
			try {
				List<Assignment> dataList = new ArrayList<Assignment>();
				dataList = assdatasource.searchAssignmentsBySubjects(selectedId);
				assdatasource.close();

				if(dataList!=null){
					AssignmentAdapter adapter = new AssignmentAdapter(this,dataList);
					list.setAdapter(adapter);
				}
				if(dataList.size()<1){

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,noFoundMsg);
					list.setAdapter(adapter);
				}

			/*	Toast.makeText(thisContext,"BySubjects:"+dataList.size(), 
						Toast.LENGTH_SHORT).show();*/
			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				assdatasource.close();
			}
		}
	}


	public void searchItemByDate(String date){
		if(academic_id>0){

			assdatasource.open();

			//search if existing record regarding academic id
			try {
				List<Assignment> dataList = new ArrayList<Assignment>();
				dataList = assdatasource.searchAssignmentsByDate(date);
				assdatasource.close();

				if(dataList!=null){
					AssignmentAdapter adapter = new AssignmentAdapter(this,dataList);
					list.setAdapter(adapter);
				}

				if(dataList.size()<1){

					ArrayAdapter<String> adapter = new ArrayAdapter<String>
					(thisContext,android.R.layout.simple_spinner_item,noFoundMsg);
					list.setAdapter(adapter);
				}
				/*Toast.makeText(thisContext,"ByDate:"+dataList.size(), 
						Toast.LENGTH_SHORT).show();*/

			} catch (Exception e) {

				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();

				assdatasource.close();
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

	/*	 (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.search_option,menu);
		return super.onCreateOptionsMenu(menu);
	}

	 (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.optionSearch:
			Intent intent = new Intent(this, SearchActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	 */
}
