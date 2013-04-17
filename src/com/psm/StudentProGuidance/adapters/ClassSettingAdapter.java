/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.Calendar;
import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.entities.Schedule;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * This class handles
 * - represent layout ("class_settingrow")
 * - as every row in the class setting list
 * 
 * Activity WITH
 * -ClassSettingListActivity
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
public class ClassSettingAdapter extends BaseAdapter {

	private final Context context;
	public static List<Schedule> schedules;
	private final static String HERE = "CLASS SETTING ADAPTER";
	private int mHour,mMinutes;
	private final int TIME_DIALOG =0;
	private final static int BEGIN =1;
	private final int END =2;
	private static int button;
	private BaseAdapter adapter = this;

	public ClassSettingAdapter(Context context,List<Schedule> schedules) {
		this.context = context;
		ClassSettingAdapter.schedules = schedules;
		//Get current System date
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR);
		mMinutes = c.get(Calendar.MINUTE);

	}

	static class ViewHolder {
		public TextView lblIndex;
		public Button btnBegin;
		public Button btnEnd;
		public int position;

	}
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, andmroid.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.class_settingrow, parent, false);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.lblIndex = (TextView) rowView.findViewById(R.id.lblIndex_CSR);
			viewHolder.btnBegin = (Button) rowView.findViewById(R.id.btnBegin_CSR);
			viewHolder.btnEnd = (Button) rowView.findViewById(R.id.btnEnd_CSR);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		String index = String.valueOf(schedules.get(position).getIndex());
		holder.lblIndex.setText(index);
		holder.position = Integer.parseInt(index);
		holder.position = position;
		holder.btnBegin.setText(schedules.get(position).getBegin());
		holder.btnEnd.setText(schedules.get(position).getEnd());
		holder.btnEnd.setOnClickListener(new ButtonOnClick(holder));
		holder.btnBegin.setOnClickListener(new  ButtonOnClick(holder));
	
		return rowView;
	}

	private class ButtonOnClick implements OnClickListener{

		private ViewHolder holder;
		public ButtonOnClick(ViewHolder holder){
			this.holder = holder;
		}

		public void onClick(View view) {

			switch(view.getId()){

			case R.id.btnBegin_CSR :
				button = BEGIN;
				Dialog dialogBegin = onCreateDialog(TIME_DIALOG,holder);
				dialogBegin.show();
				break;

			case R.id.btnEnd_CSR :
				button = END;
				Dialog dialogEnd = onCreateDialog(TIME_DIALOG,holder);
				dialogEnd.show();
				break;
			}
		}
	}

	//Show DatePicker Dialog
	protected Dialog onCreateDialog(int id, ViewHolder holder) {

		switch(id){

		case TIME_DIALOG :

			timePickerListener listener = new timePickerListener(holder);
			//set current time into timepicker
			return new TimePickerDialog(context, 
					listener, mHour, mMinutes,false);

		default:
			return null;
		}

	}

	private class timePickerListener implements TimePickerDialog.OnTimeSetListener{

		private ViewHolder holder;
		public timePickerListener(ViewHolder holder){
			this.holder=holder;
			/*Log.d(HERE,"Set Time --> Position-"+ holder.position
					+" btnBegin-"+holder.btnBegin.getText().toString()
					+" btnEnd-"+holder.btnEnd.getText().toString()
					);*/
		}

		public void onTimeSet(TimePicker view, 
				int selectedHour, int selectedMinute) {

			// Month is 0 based, just add 1
			final StringBuilder newString =
					new StringBuilder()
			.append(pad(selectedHour))
			.append(":").append(pad(selectedMinute));

			String timeDisplay = newString.toString();

			if(button==BEGIN){
				schedules.get(holder.position).setBegin(timeDisplay);
				adapter.notifyDataSetChanged();

			}else{
				schedules.get(holder.position).setEnd(timeDisplay);
				adapter.notifyDataSetChanged();
				
			}

		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public int getCount() {

		return schedules.size();
	}

	public Object getItem(int position) {

		return schedules.get(position);
	}


	public long getItemId(int position) {

		return position;
	}


}
