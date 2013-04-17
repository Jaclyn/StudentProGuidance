package com.psm.StudentProGuidance.adapters;
/**
 * 
 */

import java.util.Calendar;
import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.entities.Class;
import com.psm.StudentProGuidance.entities.Schedule;
import com.psm.StudentProGuidance.entities.Subjects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.widget.TextView;

/**
 * This class handles
 * - represent layout ("holidayrow")
 * - as every row in the holiday
 * - show the list of holiday of the user login
 * - delete the selected holiday on the list
 * 
 * Activity WITH
 * -HolidayListActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class TimeTableAdapter extends ArrayAdapter<Schedule> {

	private final Context context;

	private final List<Class> classSet;
	private final List<Schedule> scheduleSet;
	private final List<Subjects> subjectsSet;
	private final List<Integer> dayValueList;
	private int colNo;

	public TimeTableAdapter(Context context, List<Class> classSet,
			List<Schedule> scheduleSet,  List<Subjects> subjectsSet
			,List<Integer> dayValueList) {
		super(context, R.layout.timetable_item, scheduleSet);

		this.context = context;
		this.classSet = classSet;
		this.scheduleSet = scheduleSet;
		this.subjectsSet = subjectsSet;
		this.dayValueList = dayValueList;

		colNo = dayValueList.size()+1;
	}

	static class ViewHolder {
		public TextView text;
		public int position;

	}
	/*	 (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)

	@Override*/
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.timetable_item, parent, false);
			final ViewHolder holder;
			holder = new ViewHolder();
			holder.text = (TextView) rowView.findViewById(R.id.class_TTI);
			rowView.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.position = position;
		
		if((scheduleSet.get(position).getId()<1)&&
				((position<colNo)
			||(position>=colNo&&position%colNo==0))){
		
			String text =scheduleSet.get(position).getText();
			holder.text.setText(text);		
		}else{
			long schedule_id = scheduleSet.get(position).getId();
			holder.text.setText("");	
			for(int index=0;index<classSet.size();index++){
				if((classSet.get(index).getTime_from()==schedule_id
						||classSet.get(index).getTime_to()==schedule_id
						||(schedule_id>classSet.get(index).getTime_from()&&
								schedule_id<classSet.get(index).getTime_to()))
						&&classSet.get(index).getDay()==scheduleSet.get(position).getDay())
					writeText(holder,classSet.get(index));
			}
			//Class with match
			
			
		}
		this.notifyDataSetChanged();
		return rowView;
	}

	public void writeText(ViewHolder holder,Class thisClass){

		long subjects_id = thisClass.getSubjects_id();
		int index;
		for(index=0;index<subjectsSet.size();index++){
			if(subjectsSet.get(index).getId()==subjects_id){
				break;
			}
		}
		Subjects thisSubjects = subjectsSet.get(index);

		StringBuilder words = new StringBuilder();
		words
		.append(thisSubjects.getAbbrev()).append("\n")
		.append(thisClass.getTitle());

		holder.text.setText(words.toString());		
		this.notifyDataSetChanged();

	}

	public void writeLabels(int index,String text){
		//holder.text.setText(text.toString());		
		this.notifyDataSetChanged();
	}


	public void changeBackground(View view){

	}


}
