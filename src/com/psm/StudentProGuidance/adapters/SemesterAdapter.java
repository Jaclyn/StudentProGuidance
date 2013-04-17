/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.entities.Academic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
 * - represent layout ("semesterrow")
 * - as every row in the semester profile
 * - show the list of semester of the user login
 * - delete the selected semester on the list
 * - enter the main menu of selected semester
 * 
 * Activity WITH
 * -UserChooseSemActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - academic_id
 * - semester
 * 
 * @author Ng Xin Man
 */
public class SemesterAdapter  extends ArrayAdapter<Academic> {

	private final Context context;
	private final List<Academic> values;
	private AcademicDataSource datasource;
	private final String TAG = "SEMESTER ADAPTER";
	private int thisposition;

	public SemesterAdapter(Context context
			, List<Academic> values) {
		super(context, R.layout.semesterrow, values);
		this.context = context;
		this.values = values;
		datasource = new AcademicDataSource(context);
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
			rowView = inflater.inflate(R.layout.semesterrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.lblSemester_S);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.imgbtnDelete_S);

			rowView.setTag(viewHolder);
		}

		rowView.setClickable(true);

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		String display = "Semester "
				+ String.valueOf(values.get(position).getSemester());

		holder.text.setText(display);
		holder.position = position;

		holder.text.setOnClickListener(new OnClickListener(){

			public void onClick(View view) {
				try{

					handleRowOnClick(holder);

				}catch(Exception e){
					Toast toast = Toast.makeText(context, ""+e.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();
					datasource.close();
				}
			}
		});


		holder.imgbtn.setOnClickListener(new OnClickListener(){

			public void onClick(View view) {
				try{

					handleOnClick(holder);

				}catch(Exception e){
					Log.d(TAG,""+e.getMessage());
					Toast toast = Toast.makeText(context, ""+e.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();
					datasource.close();
				}
			}
		});


		return rowView;
	}

	/**
	 * This method ask for confirmation for delete
	 * @param view
	 * @throws Exception
	 */
	public void handleOnClick(ViewHolder view) throws Exception{

		ViewHolder thisholder = view;

		thisposition = thisholder.position;

		AlertDialog alert = new AlertDialog.Builder(context).create();
		alert.setTitle("Confirmation");
		alert.setMessage("Confirm Delete? All the related profile information will be deleted.");
		alert.setButton("Yes", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) { 
				deleteRow(thisposition);
				return;
			} });   
		alert.setButton2("No", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
				return;  
			} });  
		alert.show();	

	}

	/**
	 * This event call for delete selected row in database
	 * @param position
	 */
	public void deleteRow(int position){

		try {

			//delete academic record
			datasource.open();
			datasource.deleteAcademic(values.get(position));
			datasource.close();
			Log.d(TAG,"Successfully Delete!");
			values.remove(thisposition);
			Log.d(TAG,"Successfully Remove");
			notifyDataSetChanged();

			//delete subjects record
			//delete lecturer record
			//delete assignment record
			//delete class record
			//delete test record

		} catch (Exception e) {
			Log.d(TAG,""+e.getMessage());
			Toast toast = Toast.makeText(context, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			datasource.close();
		}


	}

	/**
	 * This event pass the bundle to main Menu
	 * and go tho the page of user main menu
	 * @param view
	 */
	public void handleRowOnClick(ViewHolder view){

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;
		long academic_id = values.get(thisposition).getId();
		int semester = values.get(thisposition).getSemester();

		Bundle bundleTo = new Bundle();
		bundleTo.putLong("academic_id", academic_id);
		bundleTo.putInt("semester", semester);
		Intent mainmenu = new Intent("android.intent.action.MAINMENU");
		mainmenu.putExtras(bundleTo);
		context.startActivity(mainmenu);

	}
}
