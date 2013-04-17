/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.AssignmentDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Assignment;
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
import android.widget.Toast;

/**
 * This class handles
 * - represent layout ("assignmentrow")
 * - as every row in the assignment profile
 * - show the list of assignment 
 * - delete the selected assignment on the list
 * - go to AssignmentForUpdateActivity
 * 
 * Activity WITH
 * -AssignmentListActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - academic_id
 * - assignment_id
 * 
 * @author Ng Xin Man
 */
public class AssignmentAdapter extends ArrayAdapter<Assignment> {

	private final Context context;
	private final List<Assignment> values;
	private AssignmentDataSource assdatasource;
	private SubjectsDataSource subdatasource;
	private final String HERE = "ASSIGNMENT ADAPTER";

	public AssignmentAdapter(Context context,
			List<Assignment> values) {
		super(context,R.layout.assignmentrow, values);
		this.context = context;
		this.values = values;
		assdatasource = new AssignmentDataSource(context);
		subdatasource = new SubjectsDataSource(context);
	}

	static class ViewHolder {
		public TextView text;
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
			rowView = inflater.inflate(R.layout.assignmentrow, parent, false);

			final  ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.lblInfo_ASR);
			viewHolder.alarmIcon = (ImageView) 	rowView.findViewById(R.id.alarm_ASR);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.btnDelete_ASR);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		subdatasource.open();
		Subjects thisSubjects = new Subjects();
		try {
			thisSubjects = subdatasource.getCertainSubjects(values.get(position).getSubjects_id());
			subdatasource.close();
		} catch (Exception e) {
			Log.e(HERE,""+e.getLocalizedMessage());
			subdatasource.close();
		}
		StringBuilder display = new StringBuilder()
		.append("Subjects:")
		.append("Code->").append(thisSubjects.getCode()).append("\n")
		.append("Abbrev->").append(thisSubjects.getAbbrev()).append("\n")
		.append("Name->").append(thisSubjects.getName()).append("\n").append("\n")
		.append("Title:").append(values.get(position).getTitle()).append("\n")
		.append("Submission Date: ").append(values.get(position).getDueDate()).append("\n")
		.append("Submission Time: ").append(values.get(position).getDueTime());


		if(values.get(position).getAlarm()==null||values.get(position).getAlarm().equals("")){
			holder.alarmIcon.setVisibility(View.INVISIBLE);
		}else{
			holder.alarmIcon.setVisibility(View.VISIBLE);
		}

		holder.text.setText(display.toString());
		holder.position = position;

		holder.text.setOnClickListener(new RowOnClick(holder));

		holder.imgbtn.setOnClickListener(new OnClickListener(){

			public void onClick(View view) {
				try{

					handleOnClick(holder);

				}catch(Exception error){
					Log.d(HERE,""+error.getMessage());
					Toast toast = Toast.makeText(context, ""+error.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();
					assdatasource.close();
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
				Log.d(HERE,""+error.getMessage());
				Toast toast = Toast.makeText(context, ""+error.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				assdatasource.close();
			}

		}

	}
	public void handleOnClick(ViewHolder view) throws Exception{

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;

		assdatasource.open();
		assdatasource.deleteAssignment(values.get(thisposition));
		assdatasource.close();

		values.remove(thisposition);

		notifyDataSetChanged();

	}

	public void handleRowOnClick(ViewHolder view){

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;
		long academic_id = values.get(thisposition).getAcademic_id();
		long ass_id = values.get(thisposition).getId();

		Bundle bundleTo = new Bundle();
		bundleTo.putLong("academic_id", academic_id);
		bundleTo.putLong("assignment_id", ass_id);
		Intent ass = new Intent("android.intent.action.ASSIGNMENTUPDATE");
		ass.putExtras(bundleTo);
		context.startActivity(ass);
	}

}
