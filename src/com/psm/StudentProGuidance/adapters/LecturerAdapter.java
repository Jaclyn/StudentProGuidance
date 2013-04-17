/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.LecturerDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Lecturer;

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
 * - represent layout ("lecturerrow")
 * - as every row in the lecturer profile
 * - show the list of lecturer 
 * - delete the selected lecturer on the list
 * - go to LecturerForUpdateActivity
 * 
 * Activity WITH
 * -LecturerListActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * - academic_id
 * - lecturer_id
 * 
 * @author Ng Xin Man
 */
public class LecturerAdapter  extends ArrayAdapter<Lecturer> {

	private final Context context;
	private final List<Lecturer> values;
	private LecturerDataSource lecdatasource;
	private SubjectsDataSource subdatasource;
	private final String TAG = "LECTURER ADAPTER";
	
	public LecturerAdapter(Context context
			, List<Lecturer> values) {
		super(context, R.layout.lecturerrow, values);
		this.context = context;
		this.values = values;
		lecdatasource = new LecturerDataSource(context);
		subdatasource = new SubjectsDataSource(context);
	}

	static class ViewHolder {
		public TextView txtSubjects;
		public TextView txtName;
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
			rowView = inflater.inflate(R.layout.lecturerrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtSubjects = (TextView) rowView.findViewById(R.id.lblSubjects_LR);
			viewHolder.txtName = (TextView) rowView.findViewById(R.id.lblName_LR);
			viewHolder.imgbtn = (ImageButton) rowView.findViewById(R.id.imgbtnDelete_LR);

			rowView.setTag(viewHolder);
		}

		//rowView.setClickable(true);

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		subdatasource.open();
		long subjects_id = values.get(position).getSubjects_id();

		try {

			String subjects_code =
					subdatasource.getCertainSubjects(subjects_id).getCode();
			String subjects_name = 
					subdatasource.getCertainSubjects(subjects_id).getName();
			String subjects_abbrev = 
					subdatasource.getCertainSubjects(subjects_id).getAbbrev();
			subdatasource.close();

			String subjects = "Subjects:"+subjects_code
					+"-"+subjects_name
					+"("+subjects_abbrev+")";

			holder.txtSubjects.setText(subjects);

		} catch (Exception e) {
			Log.d(TAG,""+e.getMessage());
			Toast toast = Toast.makeText(context, ""+e.getLocalizedMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
			subdatasource.close();
		}		

		String display = "Name: "+ values.get(position).getName();

		holder.txtName.setText(display);
		holder.position = position;

		holder.txtSubjects.setOnClickListener(new RowOnClickListener(holder));
		holder.txtName.setOnClickListener(new RowOnClickListener(holder));


		holder.imgbtn.setOnClickListener(new OnClickListener(){

			public void onClick(View view) {
				try{

					handleOnClick(holder);

				}catch(Exception error){
					Log.d(TAG,""+error.getMessage());
					Toast toast = Toast.makeText(context, ""+error.getLocalizedMessage(), 
							Toast.LENGTH_SHORT);
					toast.show();
					lecdatasource.close();
				}
			}
		});


		return rowView;
	}

	public void handleOnClick(ViewHolder view) throws Exception{

		ViewHolder thisholder = view;

		int thisposition = thisholder.position;

		lecdatasource.open();
		lecdatasource.deleteLecturer(values.get(thisposition));

		lecdatasource.close();
		Log.d(TAG,"Successfully Delete!");
		values.remove(thisposition);
		Log.d(TAG,"Successfully Remove");
		notifyDataSetChanged();

	}

	private class RowOnClickListener implements OnClickListener {
		
		private ViewHolder thisholder;
		public RowOnClickListener(ViewHolder holder){
			this.thisholder = holder;
		}

		public void onClick(View view) {

			int thisposition = thisholder.position;
			long academic_id = values.get(thisposition).getAcademic_id();
			long lecturer_id = values.get(thisposition).getId();

			Bundle bundleTo = new Bundle();
			bundleTo.putLong("academic_id", academic_id);
			bundleTo.putLong("lecturer_id", lecturer_id);
			Intent intent = new Intent("android.intent.action.LECTURERUPDATE");
			intent.putExtras(bundleTo);
			context.startActivity(intent);


		}

	}

}
