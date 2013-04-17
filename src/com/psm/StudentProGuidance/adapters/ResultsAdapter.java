/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.ResultsDataSource;
import com.psm.StudentProGuidance.entities.Results;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import android.widget.TextView;

/**
 * This class handles
 * - represent layout ("resultsrow")
 * - show the list of results of the certain subjects
 * - delete the selected results on the list
 * 
 * Activity WITH
 * -ResultsListActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class ResultsAdapter extends ArrayAdapter<Results> {

	private final Context context;
	private final List<Results> values;
	private final ResultsDataSource resdatasource;

	public ResultsAdapter(Context context, List<Results> values) {
		super(context, R.layout.resultsrow, values);
		this.context = context;
		this.values = values;
		resdatasource = new ResultsDataSource(context);
		}

	static class ViewHolder {
		public TextView text;
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
			rowView = inflater.inflate(R.layout.resultsrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.lblInfo_RESR);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.imgbtnDelete_RESR);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		Results thisResults = values.get(position);
		
		StringBuilder display = new StringBuilder()
		.append("Title : ")
		.append(thisResults.getTitle()).append("(")
		.append(thisResults.getPercent()).append("%)\n")
		.append("Marks : ")
		.append(thisResults.getMarks_S()).append("/")
		.append(thisResults.getMarks_B()).append(" = ")
		.append(thisResults.getMarks());
		
		holder.text.setText(display.toString());
	
		holder.position = position;
		holder.imgbtn.setOnClickListener(new OnClickListener(){

			public void onClick(View view) {
				try{
					Log.d("Adapter","Clicking");

					handleOnClick(holder);

				}catch(Exception error){
					Log.d("Adapter - Error",""+error.getMessage());
					
					Toast toast = Toast.makeText(context, ""+error.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();
					resdatasource.close();
				}
			}
		});

		return rowView;
	}

	/**
	 * This method handle the delete event of button on click
	 * @param view
	 * @throws Exception
	 */
	public void handleOnClick(ViewHolder view) throws Exception{

		resdatasource.open();
		ViewHolder thisholder = view;
		Log.d("AdapterHandle","ViewHolder : "+thisholder);
		int thisposition = thisholder.position;

		Log.d("AdapterHandle","position : "+thisposition);

		resdatasource.deleteResults(values.get(thisposition));

		resdatasource.close();
		Log.d("AdapterHandle","Successfully Delete!");
		values.remove(thisposition);
		Log.d("AdapterHandle","Successfully Remove");
		notifyDataSetChanged();

	}


}
