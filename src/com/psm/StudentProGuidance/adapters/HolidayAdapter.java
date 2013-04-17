/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.HolidayDataSource;
import com.psm.StudentProGuidance.entities.Holiday;

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
public class HolidayAdapter extends ArrayAdapter<Holiday> {

	private final Context context;
	private final List<Holiday> values;
	private HolidayDataSource datasource;

	public HolidayAdapter(Context context, List<Holiday> values) {
		super(context, R.layout.holidayrow, values);
		this.context = context;
		this.values = values;
		datasource = new HolidayDataSource(context);
	}

	static class ViewHolder {
		public TextView text;
		public TextView lblDate;
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
			rowView = inflater.inflate(R.layout.holidayrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.lblTitle_HR);
			viewHolder.lblDate  = (TextView) rowView.findViewById(R.id.lblDate_HR);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.imgbtnDelete_HR);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		Log.d("Adapter - Before","ViewHolder : "+holder);

		String title = values.get(position).getTitle();
		holder.text.setText(title);
		
		StringBuilder display = new StringBuilder();
		display.append(values.get(position).getStartdate());

		if(values.get(position).getType()>0){
			display.append(" -> ")
			.append(values.get(position).getEnddate());
		}

		holder.lblDate.setText(display);
		holder.position = position;

		Log.d("Adapter - Before","ViewHolder - imgbtn : "+holder.imgbtn);

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
					datasource.close();
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

		datasource.open();
		ViewHolder thisholder = view;
		Log.d("AdapterHandle","ViewHolder : "+thisholder);
		int thisposition = thisholder.position;

		Log.d("AdapterHandle","position : "+thisposition);

		datasource.deleteHoliday(values.get(thisposition));

		datasource.close();
		Log.d("AdapterHandle","Successfully Delete!");
		values.remove(thisposition);
		Log.d("AdapterHandle","Successfully Remove");
		notifyDataSetChanged();

	}


}
