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

package com.psm.StudentProGuidance.adapters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.AssignmentDataSource;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.controllers.HolidayDataSource;
import com.psm.StudentProGuidance.controllers.TestDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Assignment;
import com.psm.StudentProGuidance.entities.Holiday;
import com.psm.StudentProGuidance.entities.Test;
import com.psm.StudentProGuidance.entities.Class;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarAdapter extends BaseAdapter {
	static final int FIRST_DAY_OF_WEEK = 0; // Sunday = 0, Monday = 1
	static final String TAG = "CALENDAR ADAPTER";
	private Context mContext;

	private java.util.Calendar month;
	private ArrayList<String> items;
	public String[] days;
	private Calendar startSem;
	private Calendar endSem;
	private List<Integer> dayVClass;
	private List<Calendar> holidays;
	private List<Calendar> assignments;
	private List<Calendar> tests;
	private AcademicDataSource acadata;
	private ClassDataSource cladata;
	private HolidayDataSource holidata;
	private AssignmentDataSource assdata;
	private TestDataSource testdata;
	private long academic_id;

	public CalendarAdapter(Context c, Calendar monthCalendar, long id) {
		month = monthCalendar;
		//selectedDate = (Calendar)monthCalendar.clone();
		mContext = c;
		academic_id = id;

		holidays = new ArrayList<Calendar> ();
		assignments = new ArrayList<Calendar>();
		tests = new ArrayList<Calendar>();
		dayVClass = new ArrayList<Integer>();

		Log.d("CalendarAdapter","academic id "+academic_id);
		cladata = new ClassDataSource(c);
		acadata = new AcademicDataSource(c);
		holidata = new HolidayDataSource(c);
		assdata = new AssignmentDataSource(c);
		testdata = new TestDataSource(c);

		retrieveData();

		month.set(Calendar.DAY_OF_MONTH, 1);
		this.items = new ArrayList<String>();

		refreshDays();
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
	}

	public int getCount() {
		return days.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public void retrieveData(){

		try {
			acadata.open();
			Academic academic = new Academic();
			academic = acadata.getAcademicByID(academic_id);

			//Set the semester start date into Calendar
			String start = academic.getStartdate();
			String[] dateSplit = start.split("/");
			int startDay = Integer.parseInt(dateSplit[0]);
			int startMonth = Integer.parseInt(dateSplit[1])-1;
			int startYear = Integer.parseInt(dateSplit[2]);

			startSem = Calendar.getInstance();
			startSem.set(startYear, startMonth, startDay);

			//Set the semester end date into Calendar
			String end = academic.getEnddate();
			String[] dateSplit2 = end.split("/");
			int endDay = Integer.parseInt(dateSplit2[0]);
			int endMonth = Integer.parseInt(dateSplit2[1])-1;
			int endYear = Integer.parseInt(dateSplit2[2]);

			endSem = Calendar.getInstance();
			endSem.set(endYear, endMonth, endDay);

			acadata.close();

			/*Log.d(TAG,"START SEM "+startSem.toString());
			Log.d(TAG,"END SEM "+endSem.toString());
			 */
			holidata.open();
			List<Holiday> holidaysList = new ArrayList<Holiday>();
			holidaysList = holidata.getAllHolidays(academic_id);
			//Log.d(TAG,"holidaysList  "+holidaysList.size());
			holidata.close();

			/*//holidays = new Hashtable();*/
			if(holidaysList!=null){
				Log.d(TAG,"holidaysList  HERE");
				holidays.clear();
				for(int index = 0;index<holidaysList.size();index++){
					//Set the holiday start date into Calendar

					//Type 0 - ONE DATE
					String startHol = holidaysList.get(index).getStartdate();
					String[] splitStart = startHol.split("/");
					int startHolDay = Integer.parseInt(splitStart[0]);
					int startHolMonth = Integer.parseInt(splitStart[1])-1;
					int startHolYear = Integer.parseInt(splitStart[2]);

					Log.d(TAG,"startHol "+startHol.toString());
					Calendar holDate_Start = Calendar.getInstance();
					holDate_Start.set(startHolYear, startHolMonth, startHolDay);

					if(!holidays.contains(holDate_Start)){
						holidays.add(holDate_Start);
					}

					if(holidaysList.get(index).getType()!=0){

						String endHol = holidaysList.get(index).getEnddate();
						String[] splitEnd = endHol.split("/");
						int endHolDay = Integer.parseInt(splitEnd[0]);
						int endHolMonth = Integer.parseInt(splitEnd[1])-1;
						int endHolYear = Integer.parseInt(splitEnd[2]);

						Calendar holDate_End = Calendar.getInstance();
						holDate_End.set(endHolYear,endHolMonth,endHolDay);

						while(holDate_End.after(holDate_Start)){

							//Create another clone object in everyloop to 
							//renew the object identity.
							Calendar holDate_Modify = (Calendar)holDate_End.clone();

							if(!holidays.contains(holDate_Modify)){
								holidays.add(holDate_Modify);
							}

							holDate_End.add(Calendar.DAY_OF_YEAR, -1);
						}

					}

				}
			}

			//Toast.makeText(mContext, "HOLIDAY "+holidays.size(), Toast.LENGTH_SHORT).show();
			cladata.open();
			List<Class> classList = new ArrayList<Class>();
			classList = cladata.getAllClass(academic_id);
			cladata.close();
			if(classList!=null){

				for(int index = 0;index<classList.size();index++){

					int thisDay = classList.get(index).getDay();
					if(!dayVClass.contains(thisDay))
						dayVClass.add(thisDay);

				}
			}

			assdata.open();
			List<Assignment> assignmentList = new ArrayList<Assignment>();
			assignmentList = assdata.getAllAssignment(academic_id);
			assdata.close();
			if(assignmentList!=null){
				Log.d(TAG,"assignmentList  HERE");
				for(int index = 0;index<assignmentList.size();index++){
					//Set the holiday start date into Calendar
					String dueDate = assignmentList.get(index).getDueDate();
					String[] splitDate = dueDate.split("/");
					int assDay = Integer.parseInt(splitDate[0]);
					int assMonth = Integer.parseInt(splitDate[1])-1;
					int assYear = Integer.parseInt(splitDate[2]);

					Calendar assDate = Calendar.getInstance();
					assDate.set(assYear,assMonth,assDay);
					assignments.add(assDate);
				}
			}

			testdata.open();
			List<Test> testList = new ArrayList<Test>();
			testList = testdata.getAllTest(academic_id);
			testdata.close();
			if(testList!=null){
				Log.d(TAG,"testList  HERE");
				for(int index = 0;index<testList.size();index++){
					//Set the holiday start date into Calendar
					String testDate = testList.get(index).getDate();
					String[] splitDate = testDate.split("/");
					int examDay = Integer.parseInt(splitDate[0]);
					int examMonth = Integer.parseInt(splitDate[1])-1;
					int examYear = Integer.parseInt(splitDate[2]);
					Log.d(TAG,"testDate "+testDate);
					Calendar examDate = Calendar.getInstance();
					examDate.set(examYear,examMonth,examDay);
					tests.add(examDate);
				}
			}

		} catch (Exception e) {

			Log.e("CalendarAdapter", ""+e.getMessage());
			Toast toast = Toast.makeText(mContext, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();

			acadata.close();
			holidata.close();
			assdata.close();
			testdata.close();		
		}	
	}

	static class ViewHolder {
		public LinearLayout baseLayout;
		public TextView dayView;
		public	LinearLayout classLayout;
		public	TextView txtClass;
		public	LinearLayout assignmentLayout;
		public	TextView txtAssignment;
		public	LinearLayout testLayout;
		public TextView txtTests;

	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		//	LinearLayout baseLayout;

		if (convertView == null) {  // if it's not recycled, initialize some attributes
			LayoutInflater vi = (LayoutInflater)mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calendar_item, null);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.baseLayout = (LinearLayout) v.findViewById(R.id.layoutBase_Calendar);
			viewHolder.dayView = (TextView)v.findViewById(R.id.date_Calendar);
			viewHolder.classLayout = (LinearLayout)v.findViewById(R.id.layoutClass_Calendar);
			viewHolder.txtClass = (TextView)v.findViewById(R.id.txtClass_Calendar);
			viewHolder.assignmentLayout = (LinearLayout)v.findViewById(R.id.layoutAssignment_Calendar);
			viewHolder.txtAssignment = (TextView)v.findViewById(R.id.txtAssignment_Calendar);
			viewHolder.testLayout = (LinearLayout)v.findViewById(R.id.layoutTest_Calendar);
			viewHolder.txtTests = (TextView)v.findViewById(R.id.txtTest_Calendar);

			v.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) v.getTag();

		// disable empty days from the beginning
		if(days[position].equals("")) {
			holder.baseLayout.setClickable(false);
			holder.baseLayout.setFocusable(false);
			holder.baseLayout.setBackgroundResource(0);
			holder.classLayout.setBackgroundResource(0);
			holder.assignmentLayout.setBackgroundResource(0);
			holder.testLayout.setBackgroundResource(0);
		}
		else {

			boolean SCHOOL = false;
			boolean ASSIGNMENT = false;
			boolean SEMOPEN = false;
			boolean TEST = false;
			boolean HOLIDAY = false;
			//taskLayout.setBackgroundColor(Color.BLUE);
			Calendar thisCalendar = (Calendar) month.clone();

			//Get this box date details
			thisCalendar.set(month.get(Calendar.YEAR), month.get(Calendar.MONTH), Integer.parseInt(days[position]));

			//WHITE - SEMESTER SCHOOL DAYS
			Calendar oneDayBefore = (Calendar) startSem.clone();
			oneDayBefore.set(startSem.get(Calendar.YEAR), startSem.get(Calendar.MONTH), startSem.get(Calendar.DAY_OF_MONTH)-1);
			/*Toast.makeText(mContext, "View ID: "+convertView.getId()+" LinearLayout: "
					+v.getId(), Toast.LENGTH_SHORT).show();*/
			if(thisCalendar.after(oneDayBefore)&&thisCalendar.before(endSem)){
				SEMOPEN = true;
				holder.baseLayout.setBackgroundResource(R.drawable.white);
			}else{
				holder.baseLayout.setBackgroundResource(0);
			}



			if(SEMOPEN==true){

				//BLANK - HOLIDAY
				if(holidays!=null){
					for(int index=0;index<holidays.size();index++){

						if((thisCalendar.get(Calendar.YEAR)==holidays.get(index).get(Calendar.YEAR))
								&&(thisCalendar.get(Calendar.MONTH)==holidays.get(index).get(Calendar.MONTH))
								&&(thisCalendar.get(Calendar.DAY_OF_MONTH)==holidays.get(index).get(Calendar.DAY_OF_MONTH))){
							HOLIDAY =true;
							break;
						}
					}

					if(HOLIDAY==true)
						holder.baseLayout.setBackgroundResource(R.drawable.red);
					else
						holder.baseLayout.setBackgroundResource(R.drawable.white);
				}else{
					holder.baseLayout.setBackgroundResource(R.drawable.white);
				}

				if(dayVClass!=null){
					for(int index=0;index<dayVClass.size();index++){
						if(dayVClass.get(index)==thisCalendar.get(Calendar.DAY_OF_WEEK)){
							SCHOOL=true;
							break;
						}
					}
					if(SCHOOL==true)
						holder.classLayout.setBackgroundResource(R.drawable.ic_class_48);
					else
						holder.classLayout.setBackgroundResource(0);
				}else{
					holder.classLayout.setBackgroundResource(0);
				}

				if(assignments!=null){
					for(int index=0;index<assignments.size();index++){
						if((thisCalendar.get(Calendar.YEAR)==assignments.get(index).get(Calendar.YEAR))
								&&(thisCalendar.get(Calendar.MONTH)==assignments.get(index).get(Calendar.MONTH))
								&&(thisCalendar.get(Calendar.DAY_OF_MONTH)==assignments.get(index).get(Calendar.DAY_OF_MONTH))){

							ASSIGNMENT = true;
							break;
						}
					}
					if(ASSIGNMENT==true){
						holder.assignmentLayout.setBackgroundResource(R.drawable.ic_assignment_48);
					}else{
						holder.assignmentLayout.setBackgroundResource(0);
					}
				}else{
					holder.assignmentLayout.setBackgroundResource(0);
				}


				if(tests!=null){
					for(int index=0;index<tests.size();index++){
						if((thisCalendar.get(Calendar.YEAR)==tests.get(index).get(Calendar.YEAR))
								&&(thisCalendar.get(Calendar.MONTH)==tests.get(index).get(Calendar.MONTH))
								&&(thisCalendar.get(Calendar.DAY_OF_MONTH)==tests.get(index).get(Calendar.DAY_OF_MONTH))){
							TEST = true;
							break;
						}
					}
					if(TEST==true)
						holder.testLayout.setBackgroundResource(R.drawable.ic_test_48);
					else
						holder.testLayout.setBackgroundResource(0);
				}else{
					holder.testLayout.setBackgroundResource(0);
				}

			}else{
				holder.baseLayout.setBackgroundResource(0);
				holder.classLayout.setBackgroundResource(0);
				holder.assignmentLayout.setBackgroundResource(0);
				holder.testLayout.setBackgroundResource(0);
			}

		}

		holder.dayView.setText(days[position]);

		return v;
	}

	public void refreshDays()
	{
		// clear items
		items.clear();

		int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDay = (int)month.get(Calendar.DAY_OF_WEEK);

		// figure size of the array
		if(firstDay==1){
			days = new String[lastDay];
		}
		else {
			days = new String[lastDay+firstDay-1];
		}

		int j=FIRST_DAY_OF_WEEK;

		// populate empty days before first real day
		if(firstDay>Calendar.SUNDAY) {
			for(j=0;j<firstDay;j++) {
				days[j] = "";
			}
		}
		else {
			/*for(j=0;j<FIRST_DAY_OF_WEEK*6;j++) {
				days[j] = "";
			}*/
			//j=FIRST_DAY_OF_WEEK*6+1; // sunday => 1, monday => 7
			j = Calendar.SUNDAY;
		}

		// populate days
		int dayNumber = 1;
		for(int i=j-1;i<days.length;i++) {
			days[i] = ""+dayNumber;
			dayNumber++;
		}

		//Toast.makeText(mContext, "DAY: "+days.length, Toast.LENGTH_SHORT).show();
	}


}