/**
 * 
 */
package com.psm.StudentProGuidance.adapters;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.entities.Academic;
import com.psm.StudentProGuidance.entities.Assignment;
import com.psm.StudentProGuidance.entities.Class;
import com.psm.StudentProGuidance.entities.Schedule;
import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.entities.Summary;
import com.psm.StudentProGuidance.entities.Test;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * @author User
 *
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private Academic academic;
	private List<Class> classSet;
	private List<Assignment> assignSet;
	private List<Test> testSet;
	private List<Subjects> subSet;
	private List<Schedule> scheSet;
	private List<String> title;
	private Hashtable<Integer,Integer> groupChild;
	private static String lblClass = "Class";
	private static String lblAssignment = "Assignment";
	private static String lblTest = "Test";
	private static int catClass = 1;
	private static int catAssign = 2;
	private static int catTest = 3;

	public ExpandableListAdapter(Context context,Summary summary){

		this.context = context;

		this.classSet= new ArrayList<Class>();
		this.assignSet = new ArrayList<Assignment>();
		this.testSet =new ArrayList<Test>();
		this.subSet = new ArrayList<Subjects>();
		this.scheSet = new ArrayList<Schedule>();
		this.groupChild =  new Hashtable<Integer,Integer>();

		this.academic = summary.getAcademic();
		this.classSet= summary.getClassSet();
		this.assignSet = summary.getAssignSet();
		this.testSet = summary.getTestSet();
		this.subSet = summary.getSubSet();
		this.scheSet = summary.getScheSet();

		title = new ArrayList<String>();
		if(classSet!=null&&classSet.size()>0){
			title.add(lblClass);
			groupChild.put(title.size()-1, classSet.size());
		}
		if(assignSet!=null&&assignSet.size()>0){
			title.add(lblAssignment);
			groupChild.put(title.size()-1, assignSet.size());
		}
		if(testSet!=null&&testSet.size()>0){
			title.add(lblTest);
			groupChild.put(title.size()-1, testSet.size());
		}

	}

	public int analyseGroupType(int groupPosition){
		String groupName = title.get(groupPosition);

		if(groupName.equals(lblClass))
			return catClass;
		else if(groupName.equals(lblAssignment))
			return catAssign;
		else if(groupName.equals(lblTest))
			return catTest;

		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	public Object getChild(int groupPosition, int childPosition) {

		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	public View getChildView(int groupPosition, int childPosition, 
			boolean isLastChild, View convertView,
			ViewGroup parent) {

		TextView text = getChildTextView();
		String display = new String();
		display = "N/A";

		switch(analyseGroupType(groupPosition)){

		case 1: 
			Class thisClass = classSet.get(childPosition);
			if(thisClass!=null){
				display = makeClassChildText(thisClass);
			}
			break;

		case 2:
			/*Assignment thisAssignment = (Assignment)
			getChild(groupPosition,childPosition);*/
			Assignment thisAssignment = assignSet.get(childPosition);
			if(thisAssignment!=null){
				display = makeAssignmentChildText(thisAssignment);
			}
			break;

		case 3:
			Test thisTest = testSet.get(childPosition);
			if(thisTest!=null){
				display = makeTestChildText(thisTest);
			}

			break;
		}

		//text.setTextColor(R.drawable.white);
		text.setText(display);
		return text;
	}

	public String makeTestChildText(Test thisTest){

		String subjects = makeSubjectsText
				(thisTest.getSubjects_id());

		StringBuilder display = new StringBuilder();
		display.append(subjects)
		.append(thisTest.getTitle()).append("\n")
		.append("At ").append(thisTest.getVenue())
		.append(" -> ").append(thisTest.getDate())
		.append("(").append(thisTest.getTime()).append(")\n")
		.append("Chapter ").append(thisTest.getChapter());

		return display.toString();

	}

	public String makeAssignmentChildText(Assignment thisAssignment){

		String subjects = makeSubjectsText
				(thisAssignment.getSubjects_id());

		StringBuilder display = new StringBuilder();
		display.append(subjects)
		.append(thisAssignment.getTitle()).append("\n");
		
		if(thisAssignment.getDescription()!=null)
			display.append(thisAssignment.getDescription()).append("\n");
		
		display
		.append("Before ").append(thisAssignment.getDueDate())
		.append("(").append(thisAssignment.getDueTime()).append(")");

		return display.toString();
	}

	public String makeClassChildText(Class thisClass){

		String subjects = makeSubjectsText
				(thisClass.getSubjects_id());
		String fromTime = makeScheduleText
				(thisClass.getTime_from(),1);
		String toTime = makeScheduleText
				(thisClass.getTime_to(),2);

		StringBuilder display = new StringBuilder();
		display.append(subjects)
		.append(thisClass.getTitle()).append(" \n")
		.append("From ").append(fromTime)
		.append(" To ").append(toTime);

		return display.toString();

	}

	public String makeScheduleText(long schedule_id, int flag){

		int index=0;
		for(index=0;index<scheSet.size();index++){
			if(scheSet.get(index).getId()==schedule_id)
				break;
		}
		Schedule schedule = scheSet.get(index);

		String writeSchedule = new String();
		if(flag==1)
			writeSchedule = schedule.getBegin();
		else
			writeSchedule = schedule.getEnd();

		return writeSchedule;
	}

	public String makeSubjectsText(long subjects_id){

		int index=0;
		for(index=0;index<subSet.size();index++){
			if(subSet.get(index).getId()==subjects_id)
				break;
		}
		Subjects subjects = subSet.get(index);

		StringBuilder writeSubjects = new StringBuilder();
		writeSubjects.append("Subjects: ")
		.append(subjects.getCode()).append("-")
		.append(subjects.getName()).append("(")
		.append(subjects.getAbbrev()).append(")\n");

		return writeSubjects.toString();
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	public int getChildrenCount(int groupPosition) {

		int childCount = groupChild.get(groupPosition);

		return childCount;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	public int getGroupCount() {

		return groupChild.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	public long getGroupId(int groupPosition) {

		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	public View getGroupView(int groupPosition, boolean isExpanded, 
			View convertView, ViewGroup parent) {

		TextView text = getGroupTextView(groupPosition);
		text.setText(title.get(groupPosition));
		return text;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	public boolean hasStableIds() {

		return true;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return false;
	}

	public TextView getChildTextView() {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setTextColor(context.getResources().getColor(R.drawable.white));
		// Set the text starting position
		textView.setPadding(36, 0, 0, 0);
		textView.setText("N/A");
		return textView;
	}

	public TextView getGroupTextView(int groupPosition) {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setTextColor(context.getResources().getColor(R.drawable.white));

		int drawIcon;
		int type = analyseGroupType(groupPosition);
		if(type==1){
			drawIcon = R.drawable.ic_class_48;
		}else if(type==2){
			drawIcon = R.drawable.ic_assignment_48;
		}else{
			drawIcon = R.drawable.ic_test_48;
		}

		textView.setCompoundDrawablesWithIntrinsicBounds(drawIcon, 0, 0, 0);
		// Set the text starting position
		textView.setPadding(36, 0, 0, 0);
		textView.setText("N/A");
		return textView;
	}
}
