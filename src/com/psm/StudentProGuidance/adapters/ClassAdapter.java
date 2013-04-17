/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.controllers.ScheduleDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Class;
import com.psm.StudentProGuidance.entities.Schedule;
import com.psm.StudentProGuidance.entities.Subjects;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class handles
 * - represent layout ("classlistrow")
 * - as every row in the class profile
 * - show the list of class 
 * - delete the selected class on the list
 * - go to ClassForUpdateActivity
 * 
 * Activity WITH
 * -ClassListActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - academic_id
 * - class_id
 * 
 * @author Ng Xin Man
 */
public class ClassAdapter extends ArrayAdapter<Class> {

	private final Context context;
	private final List<Class> values;
	private ClassDataSource cladatasource;
	private SubjectsDataSource subdatasource;
	private ScheduleDataSource schedatasource;
	private final String HERE = "CLASS ADAPTER";

	public ClassAdapter(Context context,
			List<Class> values) {
		super(context,R.layout.classlistrow, values);
		this.context = context;
		this.values = values;
		cladatasource = new ClassDataSource(context);
		subdatasource = new SubjectsDataSource(context);
		schedatasource =  new ScheduleDataSource(context);		

	}

	static class ViewHolder {
		public TextView label;
		public ImageView alarmIcon;
		public ImageButton imgbtn;
		public int position;

	}
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.classlistrow, parent, false);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.label = (TextView) rowView.findViewById(R.id.lblInfo_CLR);
			viewHolder.alarmIcon = (ImageView) rowView.findViewById(R.id.alarm_CLR);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.btnDelete_CLR);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		subdatasource.open();
		schedatasource.open();
		Subjects thisSubjects = new Subjects();
		Schedule beginSchedule = new Schedule();

		Schedule endSchedule = new Schedule();

		try {
			thisSubjects = subdatasource.getCertainSubjects(values.get(position).getSubjects_id());
			subdatasource.close();
			beginSchedule = schedatasource.getCertainSchedule(
					values.get(position).getTime_from());

			endSchedule = schedatasource.getCertainSchedule(
					values.get(position).getTime_to());

			schedatasource.close();
		} catch (Exception e) {
			Log.e(HERE,""+e.getLocalizedMessage());
			subdatasource.close();
			schedatasource.close();
		}

		/*	String startTime = schedule.getBegin();
		String endTime = schedule.getEnd();*/

		StringBuilder display = new StringBuilder()
		.append("Subjects:")
		.append("Code->").append(thisSubjects.getCode()).append("\n")
		.append("Abbrev->").append(thisSubjects.getAbbrev()).append("\n")
		.append("Name->").append(thisSubjects.getName()).append("\n").append("\n")
		.append("Title:").append(values.get(position).getTitle()).append("\n");

		String day = new String();
		switch(values.get(position).getDay()){
		case 1:day="Sunday";
		break;
		case 2:day="Monday";
		break;
		case 3:day="Tuesday";
		break;
		case 4:day="Wednesday";
		break;
		case 5:day="Thursday";
		break;
		case 6:day="Friday";
		break;
		case 7:day="Saturday";
		break;
		}

		display.append("Day: ").append(day).append("\n");
		if(endSchedule!=null&&beginSchedule!=null){
			display.append("Time: ").append(beginSchedule.getBegin())
			.append(" to ").append(endSchedule.getEnd());
		}

		if(values.get(position).getAlarm()==null||values.get(position).getAlarm().equals("")){
			holder.alarmIcon.setVisibility(View.INVISIBLE);
		}else{
			holder.alarmIcon.setVisibility(View.VISIBLE);
		}

		holder.label.setText(display);
		holder.position = position;

		holder.label.setOnClickListener(new RowOnClick(holder));

		holder.imgbtn.setOnClickListener(new OnClickListener(){

			public void onClick(View view) {
				try{

					handleOnClick(holder);

				}catch(Exception error){
					Log.d(HERE,""+error.getMessage());

					cladatasource.close();
				}
			}
		});

		return rowView;
	}
	private class RowOnClick implements OnClickListener{

		ViewHolder holder;

		public RowOnClick(ViewHolder holder){
			this.holder = holder;
		}

		public void onClick(View view) {
			try{

				handleRowOnClick(holder);

			}catch(Exception error){
				Log.d(HERE,""+error.getMessage());

				cladatasource.close();
			}

		}

	}
	public void handleOnClick(ViewHolder view) throws Exception{

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;

		cladatasource.open();
		cladatasource.deleteClass(values.get(thisposition));
		cladatasource.close();

		values.remove(thisposition);

		notifyDataSetChanged();

	}

	public void handleRowOnClick(ViewHolder view){

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;
		long academic_id = values.get(thisposition).getAcademic_id();
		long class_id = values.get(thisposition).getId();

		Bundle bundleTo = new Bundle();
		bundleTo.putLong("academic_id", academic_id);
		bundleTo.putLong("class_id", class_id);
		Intent intent = new Intent("android.intent.action.CLASSUPDATE");
		intent.putExtras(bundleTo);
		context.startActivity(intent);
	}

}
