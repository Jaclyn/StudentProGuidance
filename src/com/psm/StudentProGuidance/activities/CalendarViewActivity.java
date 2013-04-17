/*
 * Copyright 2011 Lauri Nevala.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.psm.StudentProGuidance.activities;

import java.util.Calendar;

import com.psm.StudentProGuidance.adapters.CalendarAdapter;
import com.psm.StudentProGuidance.others.SimpleGestureFilter;
//import com.psm.StudentProGuidance.others.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
//import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;


/*public class CalendarViewActivity extends Activity implements
SimpleGestureListener{*/
public class CalendarViewActivity extends Activity {
	private TextView title;
	private TextView next;
	private TextView previous;
	private GridView gridview;
	private Calendar month;
	private CalendarAdapter adapter;
	private long academic_id;
	private int semester;
	//private SimpleGestureFilter gestureScanner;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		//gestureScanner = new SimpleGestureFilter(this,this);

		month = Calendar.getInstance();
		// onNewIntent(getIntent());

		Bundle from = getIntent().getExtras();
		academic_id = from.getLong("academic_id");
		semester = from.getInt("semester");

		// items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month,academic_id);

		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		//  handler = new Handler();
		// handler.post(calendarUpdater);

		title  = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

		previous  = (TextView) findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
				}
				refreshCalendar();
			}
		});

		next  = (TextView) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
				}
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				TextView date = (TextView)v.findViewById(R.id.date_Calendar);
				
				if(date instanceof TextView && !date.getText().equals("")) {

					Intent intent = new Intent("android.intent.action.DAYVIEW");
					String day = date.getText().toString();
					if(day.length()==1) {
						day = "0"+day;
					}
					// return chosen date as string format 
					Bundle bundle= new Bundle();
					String today = day+"/"+String.valueOf(month.get(Calendar.MONTH)+1)
							+"/"+String.valueOf(month.get(Calendar.YEAR));
					//String today = android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day;
					bundle.putLong("academic_id", academic_id);
					bundle.putString("today",today);
					//Log.d("Calendar"," today : "+today);

					intent.putExtras(bundle);
					startActivity(intent);

					//setResult(RESULT_OK, intent);
					//finish();
				}else{
					Log.d("Calendar","Clicked");
				}

			}
		});
	}

	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		//handler.post(calendarUpdater); // generate some random calendar items				

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public void onSwipe(int direction) {
		switch (direction) {

		case SimpleGestureFilter.SWIPE_RIGHT : 
			Intent mainmenu = new Intent("android.intent.action.MAINMENU");
			Bundle bundle = new Bundle();
			bundle.putLong("academic_id", academic_id);
			bundle.putInt("semester", semester);
			mainmenu.putExtras(bundle);
			startActivity(mainmenu);
			finish();

			break;
		case SimpleGestureFilter.SWIPE_LEFT :  


			break;
		case SimpleGestureFilter.SWIPE_DOWN :  

			break;
		case SimpleGestureFilter.SWIPE_UP :    

			break;

		} 
	}


	/*
	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		this.gestureScanner.onTouchEvent(me);
		return super.dispatchTouchEvent(me); 
	}*/

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.calendar_option,menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.optionHelp:
			Intent intent = new Intent(this, CalendarGuideActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onDoubleTap() {
		// TODO Auto-generated method stub

	}

	/*public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	}*/

	/*public Runnable calendarUpdater = new Runnable() {

		public void run() {
			items.clear();
			// format random values. You can implement a dedicated class to provide real values
			for(int i=0;i<31;i++) {
				Random r = new Random();

				if(r.nextInt(10)>6)
				{
					items.add(Integer.toString(i));
				}
			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};*/
}
