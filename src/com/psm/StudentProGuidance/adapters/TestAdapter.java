/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.controllers.TestDataSource;
import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.entities.Test;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class handles
 * - represent layout ("testrow")
 * - as every row in the test profile
 * - show the list of test 
 * - delete the selected test on the list
 * - go to TestForUpdateActivity
 * 
 * Activity WITH
 * -TestListActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - academic_id
 * - test_id
 * 
 * @author Ng Xin Man
 */
public class TestAdapter extends ArrayAdapter<Test> {

	private final Context context;
	private final List<Test> values;
	private TestDataSource testdatasource;
	private SubjectsDataSource subdatasource;
	private final String HERE = "TEST ADAPTER";

	public TestAdapter(Context context,
			List<Test> values) {
		super(context,R.layout.testrow, values);
		this.context = context;
		this.values = values;
		testdatasource = new TestDataSource(context);
		subdatasource = new SubjectsDataSource(context);
	}

	static class ViewHolder {
		public TextView txtTitle;
		public TextView txtTag;
		public TextView txtDetails;
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
			rowView = inflater.inflate(R.layout.testrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtTitle = (TextView) rowView.findViewById(R.id.lblInfo_TR);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.btnDelete_TR);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();
		StringBuilder tag = new StringBuilder();
		
		long subjects_id = values.get(position).getSubjects_id();
		subdatasource.open();
		Subjects subjects;
		try {
			subjects = subdatasource.getCertainSubjects(subjects_id);
			subdatasource.close();
			if(subjects!=null){
				String subCode = subjects.getCode();
				String subName = subjects.getName();
				String subAbbrev = subjects.getAbbrev();

				tag
				.append("Subjects:")
				.append("Code->").append(subCode).append("\n")
				.append("Abbrev->").append(subAbbrev).append("\n")
				.append("Name->").append(subName).append("\n");
				
			}
		} catch (Exception e) {
			Log.d(HERE,""+e.getMessage());
		}
		
		tag.append("\n")
		.append("Title: ").append(values.get(position).getTitle()).append("\n")
		.append("Chapter: ");
		
		String[] splitChap = values.get(position).getChapter().split("-");
		if(Integer.parseInt(splitChap[0])==Integer.parseInt(splitChap[1])){
			tag.append(splitChap[0]).append("\n");
		}else{
			tag.append(values.get(position).getChapter()).append("\n");
		}

		String date = values.get(position).getDate();
		String time = values.get(position).getTime();
		String venue = values.get(position).getVenue();

		tag
		.append("Date: ").append(date).append("\n")
		.append("Time: ").append(time).append("\n")
		.append("Venue: ").append(venue);


		holder.txtTitle.setText(tag);
		holder.position = position;
	
		holder.txtTitle.setOnClickListener(new RowOnClickListener(holder));

		holder.imgbtn.setOnClickListener(new OnClickListener(){

			public void onClick(View view) {
				try{

					handleOnClick(holder);

				}catch(Exception error){
					Log.d(HERE,""+error.getMessage());
					Toast toast = Toast.makeText(context, ""+error.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();
					testdatasource.close();
				}
			}
		});

		return rowView;
	}

	private class RowOnClickListener implements OnClickListener{

		private ViewHolder holder;

		public RowOnClickListener(ViewHolder holder){
			this.holder = holder;
		}

		public void onClick(View view) {
			try{

				handleRowOnClick(holder);

			}catch(Exception error){
				Log.d(HERE,""+error.getMessage());
				Toast toast = Toast.makeText(context, ""+error.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				testdatasource.close();
			}
		}

	}
	public void handleOnClick(ViewHolder view) throws Exception{

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;

		testdatasource.open();
		testdatasource.deleteAssignment(values.get(thisposition));
		testdatasource.close();

		values.remove(thisposition);

		notifyDataSetChanged();

	}

	public void handleRowOnClick(ViewHolder view){

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;
		long academic_id = values.get(thisposition).getAcademic_id();
		long test_id = values.get(thisposition).getId();

		Bundle bundleTo = new Bundle();
		bundleTo.putLong("academic_id", academic_id);
		bundleTo.putLong("test_id", test_id);
		Intent intent = new Intent("android.intent.action.TESTUPDATE");
		intent.putExtras(bundleTo);
		context.startActivity(intent);
	}

}
