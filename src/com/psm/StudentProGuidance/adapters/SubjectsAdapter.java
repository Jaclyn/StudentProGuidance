/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Subjects;

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
import android.widget.Toast;

import android.widget.TextView;

/**
 * @author User
 *
 */
public class SubjectsAdapter extends ArrayAdapter<Subjects> {

	private final Context context;
	private final List<Subjects> values;
	private SubjectsDataSource datasource;
	private int thisposition;
	private final String TAG ="Subjects Adapter";

	public SubjectsAdapter(Context context
			, List<Subjects> values) {
		super(context, R.layout.subjectsrow, values);
		this.context = context;
		this.values = values;
		datasource = new SubjectsDataSource(context);
	}

	static class ViewHolder {
		public TextView txtCodeNameAbbrev;
		public TextView txtCreditChapter;
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
			rowView = inflater.inflate(R.layout.subjectsrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtCodeNameAbbrev = (TextView) rowView.findViewById(R.id.lblSubjects_SR);
			viewHolder.txtCreditChapter = (TextView) rowView.findViewById(R.id.lblSubjectsDetails_SR);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.btnDelete_SR);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		Log.d("SubjectsAdapter - Before","ViewHolder : "+holder);

		StringBuilder tag = new StringBuilder()
		.append("Code: ").append(values.get(position).getCode()).append("\n")
		.append("Abbrev: ").append(values.get(position).getAbbrev()).append("\n")
		.append("Name: ").append(values.get(position).getName()).append("\n");
		
		StringBuilder details = new StringBuilder()
		.append("Credit: ").append(values.get(position).getCredit()).append("\n")
		.append("Total Chapter: ").append(values.get(position).getTotalChapter());

		holder.txtCodeNameAbbrev.setText(tag);
		holder.txtCreditChapter.setText(details);
		holder.position = position;

		holder.txtCodeNameAbbrev.setOnClickListener(new RowOnClick(holder));
		holder.txtCreditChapter.setOnClickListener(new RowOnClick(holder));

		Log.d("SubjectsAdapter - Before","ViewHolder - imgbtn : "
				+holder.imgbtn);

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
	 * This method propogate to SubjectsForUpdateActivity
	 * @author Ng Xin Man
	 *
	 */
	private class RowOnClick implements OnClickListener{
		
		private ViewHolder holder;
		
		public RowOnClick(ViewHolder holder){
			this.holder = holder;
		}

		public void onClick(View view) {
			ViewHolder thisholder = holder;

			int thisrowposition = thisholder.position;
			long academic_id = values.get(thisrowposition).getAcademic_id();
			long subjects_id = values.get(thisrowposition).getId();

			Bundle bundleTo = new Bundle();
			bundleTo.putLong("academic_id", academic_id);
			bundleTo.putLong("subjects_id", subjects_id);
			Intent intent = new Intent("android.intent.action.SUBJECTSUPDATE");
			intent.putExtras(bundleTo);
			context.startActivity(intent);
		}

	}

	public void handleOnClick(ViewHolder view) throws Exception{

		ViewHolder thisholder = view;
		Log.d("AdapterHandle","ViewHolder : "+thisholder);
		thisposition = thisholder.position;

		AlertDialog alert = new AlertDialog.Builder(context).create();
		alert.setTitle("Confirmation");
		alert.setMessage("Confirm Delete? All the related information will be deleted.");
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

			datasource.open();
			datasource.deleteSubjects(values.get(thisposition));

			datasource.close();
			Log.d("AdapterHandle","Successfully Delete!");
			values.remove(thisposition);
			Log.d("AdapterHandle","Successfully Remove");
			notifyDataSetChanged();
		} catch (Exception e) {
			Log.d(TAG,""+e.getMessage());

			datasource.close();
		}
	}



}
