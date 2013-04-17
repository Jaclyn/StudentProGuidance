/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.Calendar;
import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.entities.Test;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This class handles
 * - represent layout ("revisionrow")
 * - as every row in the revision profile
 * - show the list of revision analysis results 
 * 
 * Activity WITH
 * -RevisoinActivity
 * 
 * Bundle IN (key)
 * - N/A
 * 
 * Bundle OUT (key)
 * -N/A
 * 
 * @author Ng Xin Man
 */
public class RevisionAdapter extends ArrayAdapter<Test> {

	private final Context context;
	private final List<Test> values;
	private final String HERE = "REVISION ADAPTER";

	public RevisionAdapter(Context context,
			List<Test> values) {
		super(context,R.layout.revisionrow, values);
		this.context = context;
		this.values = values;
	}

	static class ViewHolder {
		public TextView txtInfo;
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
			rowView = inflater.inflate(R.layout.revisionrow, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtInfo = (TextView) rowView.findViewById(R.id.lblRevisionInfo_RR);
			viewHolder.position = position;

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		Test test = values.get(position);
			//int chapterToRead;
		String chapterRange = test.getChapter();
		String chap = chapterRange;
		/*String chapters[] = chapterRange.split("-");
		if(chapters[0].equals(chapters[1])){
			chap = chapters[0];
			//chapterToRead = 1;
		}else{
			int from = Integer.parseInt(chapters[0]);
			int to = Integer.parseInt(chapters[1]);
		//	chapterToRead = to-from+1;
		}*/

		StringBuilder info = new StringBuilder()
		.append(test.getTitle()).append("\n")
		.append("Chapter: ").append(chap).append("\n")
		.append("Date: ").append(test.getDate()).append("\n")
		.append("Time: ").append(test.getTime()).append("\n")
		.append("Venue: ").append(test.getVenue()).append("\n");

		String dateTest = test.getDate();
		String[] partition = dateTest.split("/");
		int day = Integer.parseInt(partition[0]);
		int month = Integer.parseInt(partition[1])-1;
		int year = Integer.parseInt(partition[2]);

		Calendar that = Calendar.getInstance();
		that.set(year, month, day);
		Log.d(HERE,"targetDay: "+that.get(Calendar.DAY_OF_MONTH));
		Log.d(HERE,"targetMonth: "+that.get(Calendar.MONTH));
		Log.d(HERE,"targetYear: "+that.get(Calendar.YEAR));

		Calendar current = Calendar.getInstance();
		Log.d(HERE,"currentDay: "+current.get(Calendar.DAY_OF_MONTH));
		Log.d(HERE,"currentMonth: "+current.get(Calendar.MONTH));
		Log.d(HERE,"currentYear: "+current.get(Calendar.YEAR));

		Calendar clone = (Calendar) current.clone();
		int target = that.get(Calendar.DAY_OF_YEAR);
		Log.d(HERE,"target: "+target);
		int daysBetween=0;
		if(current.before(that)){
			int now = current.get(Calendar.DAY_OF_YEAR);
			Log.d(HERE,"now: "+now);
			while (clone.before(that)) {
				clone.add(Calendar.DAY_OF_YEAR, 1);
				daysBetween++;
				int adding = clone.get(Calendar.DAY_OF_YEAR);
				Log.d(HERE,"adding: "+adding);
			}
		}else if(current.after(that)){
			daysBetween = -1;
		}else{
			daysBetween = 0;
		}
		Log.d(HERE,"daysBetween: "+daysBetween);
		String days;
		if(daysBetween == -1){
			days = "PASS";
			holder.txtInfo.setTextColor(context.getResources().getColor(R.drawable.green));
		}else if(daysBetween == 0){
			days = "TODAY =) Good Luck!";
			holder.txtInfo.setTextColor(context.getResources().getColor(R.drawable.yellow));
		}else{
			days = daysBetween +"days to go";
			if(daysBetween<=10){
				holder.txtInfo.setTextColor(context.getResources().getColor(R.drawable.red));
			}else{
				holder.txtInfo.setTextColor(context.getResources().getColor(R.drawable.white));
			}
		}
		
		info.append(days);
		holder.txtInfo.setText(info);
		Log.d(HERE,days.toString());
		

		
		this.notifyDataSetChanged();

		return rowView;
	}



}
