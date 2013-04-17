/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
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
public class ChooseDayAdapter extends BaseAdapter {

	private final Context context;
	private final String[] values;
	public List<Integer> checked = new ArrayList<Integer>();
	private final String HERE = "ChooseDay ADAPTER";

	/**
	* CLASS_DAY (according to SDK)
	 * =========
	 * SUNDAY=1
	 * MONDAY=2
	 * TUESDAY=3
	 * WEDNESDAY=4
	 * THURSDAY=5
	 * FRIDAY=6
	 * SATURDAY=7
	 * **/
	
	public ChooseDayAdapter(Context context,List<Integer> defaultList) {
		super();
		this.context = context;
		this.values = context.getResources().getStringArray(R.array.weekdaysitem);
		this.checked = defaultList;
	
	}

	static class ViewHolder {
		public TextView text;
		public CheckBox box;
		public int dayValue;

	}
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.chooseday_item, parent, false);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.text_CDI);
			viewHolder.box = (CheckBox) rowView.findViewById(R.id.checkbox_CDI);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		String day = (String) getItem(position);
		holder.text.setText(day);
		holder.dayValue=position+1;
		
		if(checked.contains(new Integer(holder.dayValue))){
			holder.box.setChecked(true);
		}else{
			holder.box.setChecked(false);
		}
			
		holder.box.setOnClickListener(new CheckBoxOnClickListener(holder.dayValue));

		return rowView;
	}
	
	private class CheckBoxOnClickListener implements OnClickListener{
		
		private int position;
		public CheckBoxOnClickListener(int position){
			this.position = position;
		}

		public void onClick(View arg0) {
			setCheckedItem(position);
		}
		
	}

	public void setCheckedItem(int item) {

		if (checked.contains(new Integer(item))==false){
			checked.add(new Integer(item));
		}else{
			checked.remove(new Integer(item));
		}

		//Toast.makeText(context, String.valueOf(item), Toast.LENGTH_SHORT).show();
	}

	public List<Integer> getCheckedItems(){
		return checked;
	}


	public int getCount() {

		return this.values.length;
	}

	public Object getItem(int index) {

		return this.values[index];
	}



	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


}
