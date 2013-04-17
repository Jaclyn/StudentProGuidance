/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.ResultsDataSource;
import com.psm.StudentProGuidance.entities.Results;
import com.psm.StudentProGuidance.entities.Subjects;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class handles
 * - represent layout ("overallrow")
 * - show the list of results by subjects
 * 
 * Activity WITH
 * -OverallActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class OverallAdapter  extends ArrayAdapter<Subjects> {

	private final Context context;
	private final List<Subjects> values;
	private ResultsDataSource resdatasource;
	private final String TAG = "OVERALL ADAPTER";

	public OverallAdapter(Context context
			, List<Subjects> values) {
		super(context, R.layout.overallrow, values);
		this.context = context;
		this.values = values;
		resdatasource = new ResultsDataSource(context);
	}

	static class ViewHolder {
		public TextView text;

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
			rowView = inflater.inflate(R.layout.overallrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.lblInfo_OR);
			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		Subjects thisSubjects = values.get(position);
		Results inResults = new Results();
		inResults.setAcademic_id(thisSubjects.getAcademic_id());
		inResults.setSubjects_id(thisSubjects.getId());
		resdatasource.open();
		List<Results> results = new ArrayList<Results>();
		try {
			results = resdatasource.getAllResults(inResults);
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
			Toast toast = Toast.makeText(context, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			resdatasource.close();
		}
		
		
		StringBuilder info = new StringBuilder();
		info.append(thisSubjects.getCode()).append("-")
		.append(thisSubjects.getName()).append("(")
		.append(thisSubjects.getAbbrev()).append(")\n")
		.append("Credit: ").append(thisSubjects.getCredit()).append("\n\n");
		
		if(results!= null){
		
			if(results.size()>0){
				for(int index=0;index<results.size();index++){
					Results thisResults = results.get(index);
					info.append(thisResults.getTitle()).append("(")
					.append(thisResults.getPercent()).append("%)->")
					.append(thisResults.getMarks()).append("\n\n");
				}
			}else{
				info.append("No Results Record In this Subjects\n");
			}
			
		}else{
			
			info.append("No Results Record In this Subjects\n");
		}
		
		holder.text.setText(info);
	
		return rowView;
	}
	
}
