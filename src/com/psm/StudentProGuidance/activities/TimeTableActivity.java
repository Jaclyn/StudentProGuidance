package com.psm.StudentProGuidance.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.psm.StudentProGuidance.adapters.TimeTableAdapter;
import com.psm.StudentProGuidance.controllers.AcademicDataSource;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.controllers.ScheduleDataSource;
import com.psm.StudentProGuidance.controllers.SubjectsDataSource;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Schedule;
import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.entities.Class;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * This class will 
 * - show layout ("timetable")
 * - allow access to setting and view classes
 * 
 * Activity FROM
 * - MainMenuActivity
 * 
 * Activity TO
 * - ClassListActivity
 * 
 * Bundle IN (key)
 * - academic_id
 * 
 * Bundle OUT (key)
 * - N/A
 * 
 * @author Ng Xin Man
 */
public class TimeTableActivity extends Activity{

	private ImageButton btnSetting;
	private Button btnView;
	private GridView gridView;
	public static final String TAG = "TIME TABLE ACTIVITY";
	private ClassDataSource classdatasource;
	private AcademicDataSource acadatasource;
	private ScheduleDataSource schedatasource;
	private SubjectsDataSource subdatasource;
	private List<Class> classList;
	private List<Schedule> scheduleList;
	private List<Subjects> subjectsList;
	private long academic_id;
	private List<Integer> dayValueList;
	//private LinearLayout timeLayout;
	//private TextView lblTime;

	/*(non-Javadoc)
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);

		btnSetting = (ImageButton) findViewById(R.id.btnSetting_CTT);
		btnView  = (Button) findViewById(R.id.btnView_CTT);
		gridView = (GridView) findViewById(R.id.gridView_CTT);
		//LayoutInflater inflater = getLayoutInflater();
		//timeLayout = (LinearLayout) inflater.inflate(R.id.layoutText_CTT,null);
		//lblTime = (TextView) findViewById(R.id.lblTime_CTT);
		//table = (TableLayout) findViewById(R.id.tableCTT);
		//timeLayout = (LinearLayout) rowView.findViewById(R.id.layoutText_CTT);

		btnSetting.setOnClickListener(new ButtonOnClickListener());
		btnView.setOnClickListener(new ButtonOnClickListener());

		classdatasource = new ClassDataSource(this);
		acadatasource = new AcademicDataSource(this);
		schedatasource = new ScheduleDataSource(this);
		subdatasource = new SubjectsDataSource(this);

		classList = new ArrayList<Class>();
		scheduleList = new ArrayList<Schedule>();
		subjectsList = new ArrayList<Subjects>();
		dayValueList = new ArrayList<Integer>();

		Bundle bundle = getIntent().getExtras();
		academic_id = bundle.getLong("academic_id");

		Log.d(TAG, "academic_id : "+academic_id);
		setTimeTableIntoGrid();
	}

	/**
	 * This private class handles OnClick event.
	 * @author Ng Xin Man
	 */
	private class ButtonOnClickListener implements OnClickListener {

		public void onClick(View view) {
			switch(view.getId()){

			case  R.id.btnSetting_CTT :
				Intent setting = new Intent("android.intent.action.CLASSSETTING");
				Bundle toSetting = new Bundle();
				toSetting.putLong("academic_id", academic_id);
				setting.putExtras(toSetting);
				startActivity(setting);
				//finish();
				break;

			case  R.id.btnView_CTT :
				Intent classList = new Intent("android.intent.action.CLASSLIST");
				Bundle toClassList = new Bundle();
				toClassList.putLong("academic_id", academic_id);
				classList.putExtras(toClassList);
				startActivity(classList);
				break;

			}
		}
	}

	/**
	 * Load holiday records regarding academic id
	 */
	public void setTimeTableIntoGrid(){

		if(academic_id>0){
			Log.d(TAG, "Academic id verify!");
			//search if existing record regarding academic id
			try {

				acadatasource.open();
				Academic thisAcademic = acadatasource.getAcademicByID(academic_id);
				acadatasource.close();

				if(thisAcademic!=null){
					
					String[] splitDays = thisAcademic.getDaySet().split(",");
					dayValueList.clear();
					for(int i=0;i<splitDays.length;i++)
						dayValueList.add(Integer.parseInt(splitDays[i]));
				}

				schedatasource.open();
				scheduleList = schedatasource.getAllSchedule(academic_id);
				schedatasource.close();

				List<Schedule> schedulesTemplate = new ArrayList<Schedule>();

				//Set schedule to day
				for(int index=0;index<scheduleList.size();index++){
					int day = Integer.valueOf(Calendar.SUNDAY);

					while(day<=Calendar.SATURDAY){

						if(dayValueList.contains(day)){
							Schedule thisS = new Schedule();
							thisS.setAcademic_id(scheduleList.get(index).getAcademic_id());
							thisS.setId(scheduleList.get(index).getId());
							thisS.setBegin(scheduleList.get(index).getBegin());
							thisS.setEnd(scheduleList.get(index).getEnd());
							thisS.setIndex(scheduleList.get(index).getIndex());
							thisS.setDay(day);
							//Log.d("SCHEDULE take","INDEX:"+index+" TIME:"+thisS.getBegin()+"-"+thisS.getEnd());
							schedulesTemplate.add(thisS);
						}		
						day++;
					}

				}

				classdatasource.open();
				classList = classdatasource.getAllClass(academic_id);
				classdatasource.close();

				subdatasource.open();
				subjectsList = subdatasource.getAllSubjects(academic_id);
				subdatasource.close();

				if(schedulesTemplate!=null){

					int maxCol = dayValueList.size()+1;

					//Add blank To display schedule time

					for(int i=0;i<schedulesTemplate.size();i+=maxCol){

						String time=new String();
						String to= new String();
						time = schedulesTemplate.get(i).getBegin();
						to = schedulesTemplate.get(i).getEnd();
						time=time+"\n"+to;
						Log.d("SCHEDULE","ROW:"+i+" TIME:"+time);
						Schedule sheTime = new Schedule();
						sheTime.setText(time);
						schedulesTemplate.add(i,sheTime);

					}

					//Add blank to display day
					int elementPosition=0;
					Schedule sheDay = new Schedule();
					sheDay.setText("Day\n\\\nTime");
					schedulesTemplate.add(elementPosition,sheDay);
					elementPosition++;

					//while(elementPosition<maxCol){
					for(int j=Calendar.SUNDAY;j<=Calendar.SATURDAY;j++){

						if(dayValueList.contains(j)){
							String day=new String();
							switch(j){
							case 1:day="Sun";
							break;
							case 2:day="Mon";
							break;
							case 3:day="Tues";
							break;
							case 4:day="Wed";
							break;
							case 5:day="Thur";
							break;
							case 6:day="Fri";
							break;
							case 7:day="Sat";
							break;
							}
							Schedule sheDay2 = new Schedule();
							sheDay2.setText(day);
							schedulesTemplate.add(elementPosition,sheDay2);
							elementPosition++;
						}
					}

					//}

					TimeTableAdapter adapter = new TimeTableAdapter(this,classList,schedulesTemplate,subjectsList,dayValueList);
					gridView.setNumColumns(maxCol);
					gridView.setAdapter(adapter);

				}


			} catch (Exception e) {

				Log.e(TAG, ""+e.getMessage());
				Toast toast = Toast.makeText(this, ""+e.getLocalizedMessage(), 
						Toast.LENGTH_SHORT);
				toast.show();
				acadatasource.close();
				schedatasource.close();
				classdatasource.close();
				subdatasource.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setTimeTableIntoGrid();
	}


}
