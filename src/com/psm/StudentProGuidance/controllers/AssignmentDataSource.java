/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author User
 *
 */
public class AssignmentDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.ASSIGNMENT_ID,
			MySQLiteHelper.ASSIGNMENT_TITLE,
			MySQLiteHelper.ASSIGNMENT_SUBJECTS,
			MySQLiteHelper.ASSIGNMENT_DESCRIPTION,
			MySQLiteHelper.ASSIGNMENT_DUEDATE,
			MySQLiteHelper.ASSIGNMENT_DUETIME,
			MySQLiteHelper.ASSIGNMENT_ALARM,
			MySQLiteHelper.ASSIGNMENT_ACADEMIC};

	public AssignmentDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	/**
	 * Open database connection
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Close database connection
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Create new Assignment record
	 * @param assignment
	 * @return newly created object of Assignment
	 * @throws Exception 
	 */
	public Assignment createAssignment(Assignment assignment) throws Exception {
		try{  
			Log.d("AssignmentDataSource","Create Assignment");
			ContentValues values = new ContentValues();

			values.put(MySQLiteHelper.ASSIGNMENT_TITLE,assignment.getTitle());
			values.put(MySQLiteHelper.ASSIGNMENT_SUBJECTS, assignment.getSubjects_id());
			if(assignment.getDescription()==null||assignment.getDescription().equals("")){
				values.putNull(MySQLiteHelper.ASSIGNMENT_DESCRIPTION);
			}else{
				values.put(MySQLiteHelper.ASSIGNMENT_DESCRIPTION, assignment.getDescription());
			}
			Log.d("AssignmentDataSource","assignment description : "+assignment.getDescription());
			values.put(MySQLiteHelper.ASSIGNMENT_DUEDATE, assignment.getDueDate());
			values.put(MySQLiteHelper.ASSIGNMENT_DUETIME, assignment.getDueTime());
			if(assignment.getAlarm()==null||assignment.getAlarm().equals("")){
				values.putNull(MySQLiteHelper.ASSIGNMENT_ALARM);
			}else{
				values.put(MySQLiteHelper.ASSIGNMENT_ALARM, assignment.getAlarm());
			}
			values.put(MySQLiteHelper.ASSIGNMENT_ACADEMIC,assignment.getAcademic_id());

			long newId = database.insert(MySQLiteHelper.TABLE_ASSIGNMENT,
					null,values);

			Log.d("AssignmentDataSource","newId : "+newId);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns, MySQLiteHelper.ASSIGNMENT_ID + " = " + newId, null,
					null, null, null);
			cursor.moveToFirst();
			Assignment newAssignment = cursorToAssignment(cursor);
			cursor.close();
			return newAssignment;
		}
		catch (Exception e){
			throw e;
		}
	}


	/**
	 * Delete Assignment records by assignment id
	 * @param assignment
	 * @throws Exception 
	 */
	public void deleteAssignment(Assignment assignment) throws Exception {
		try{  
			long id = assignment.getId();

			database.delete(MySQLiteHelper.TABLE_ASSIGNMENT, MySQLiteHelper.ASSIGNMENT_ID
					+ " = " + id, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record by assignment id
	 * @param academic
	 * @throws Exception
	 */
	public void updateAssignment(Assignment assignment) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.ASSIGNMENT_TITLE,assignment.getTitle());
			dataToInsert.put(MySQLiteHelper.ASSIGNMENT_SUBJECTS, assignment.getSubjects_id());
			if(assignment.getDescription()==null||assignment.getDescription().equals("")){
				dataToInsert.putNull(MySQLiteHelper.ASSIGNMENT_DESCRIPTION);
			}else{
				dataToInsert.put(MySQLiteHelper.ASSIGNMENT_DESCRIPTION, assignment.getDescription());
			}
			dataToInsert.put(MySQLiteHelper.ASSIGNMENT_DUEDATE, assignment.getDueDate());
			dataToInsert.put(MySQLiteHelper.ASSIGNMENT_DUETIME, assignment.getDueTime());
			if(assignment.getAlarm()==null||assignment.getAlarm().equals("")){
				dataToInsert.putNull(MySQLiteHelper.ASSIGNMENT_ALARM);
			}else{
				dataToInsert.put(MySQLiteHelper.ASSIGNMENT_ALARM, assignment.getAlarm());
			}
			String where = MySQLiteHelper.ASSIGNMENT_ID + "=" + assignment.getId();

			database.update(MySQLiteHelper.TABLE_ASSIGNMENT, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record by assignment id
	 * @param academic
	 * @throws Exception
	 */
	public void updateAssignmentForTurnOffAlarm(long assignmentId) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          

			dataToInsert.putNull(MySQLiteHelper.ASSIGNMENT_ALARM);

			String where = MySQLiteHelper.ASSIGNMENT_ID + "=" + assignmentId;

			database.update(MySQLiteHelper.TABLE_ASSIGNMENT, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the assignment records by user id
	 * @param academic id
	 * @return List of Assignment records
	 * @throws Exception 
	 */
	public List<Assignment> getAllAssignment(long academic_id) throws Exception {
		List<Assignment> academics = new ArrayList<Assignment>();

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns,  MySQLiteHelper.ASSIGNMENT_ACADEMIC + " = " + academic_id,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Assignment data_assignment = cursorToAssignment(cursor);
				academics.add(data_assignment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return academics;
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the assignment records by academic id and date
	 * @param academic id
	 * @return List of Assignment records
	 * @throws Exception 
	 */
	public List<Assignment> getAllRelatedAssignment(Assignment inAssignment) throws Exception {
		List<Assignment> academics = new ArrayList<Assignment>();

		String where =  MySQLiteHelper.ASSIGNMENT_ACADEMIC + " = " 
				+ inAssignment.getAcademic_id() + " and "
				+ MySQLiteHelper.ASSIGNMENT_DUEDATE + " = '"
				+ inAssignment.getDueDate()+"'";


		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns, where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Assignment data_assignment = cursorToAssignment(cursor);
				academics.add(data_assignment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return academics;
		}
		catch (Exception e){
			throw e;
		}
	}


	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Assignment object (row)
	 */
	private Assignment cursorToAssignment(Cursor cursor) {
		Assignment assignment = new Assignment();
		assignment.setId(cursor.getLong(0));
		assignment.setTitle(cursor.getString(1));
		assignment.setSubjects_id(cursor.getLong(2));
		assignment.setDescription(cursor.getString(3));
		assignment.setDueDate(cursor.getString(4));
		assignment.setDueTime(cursor.getString(5));
		assignment.setAlarm(cursor.getString(6));
		assignment.setAcademic_id(cursor.getLong(7));
		return assignment;
	}

	/**
	 * Get the details of Assignment by its id
	 * @param assignment that found
	 * @return Assignment object
	 * @throws Exception 
	 */
	public Assignment getCertainAssignment(long assignment_id) throws Exception{

		boolean found = false;
		Assignment data_found = new Assignment();
		String where = MySQLiteHelper.ASSIGNMENT_ID + "="
				+ assignment_id;

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Assignment data_assignment = cursorToAssignment(cursor);
				data_found = data_assignment;
			}
			// Make sure to close the cursor
			cursor.close();

			if(found == true)
				return data_found;
			else
				return null;
		}
		catch (Exception e){
			throw e;
		}
	}
	
	/**
	 * This method search for assignments with match text
	 * @param text
	 * @return list of assignment with match string
	 * @throws Exception 
	 */
	public List<Assignment> searchAssignmentsByTitle(String text) throws Exception{
		Log.d("Add","Text - '"+text+"'");
		List<Assignment> assignments = new ArrayList<Assignment>();

		String where =  MySQLiteHelper.ASSIGNMENT_TITLE + " LIKE '" 
				+ text + "%'";
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns, where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Assignment data_assignment = cursorToAssignment(cursor);
				assignments.add(data_assignment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			Log.d("Add", "Search results - "+assignments.size());
			return assignments;
		}
		catch (Exception e){
			throw e;
		}
		
	}
	
	/**
	 * This method search for assignments with match subjects text
	 * @param text
	 * @return list of assignment with match string
	 * @throws Exception 
	 */
	public List<Assignment> searchAssignmentsBySubjectsText(String text) throws Exception{
		//Log.d("Add","Text - '"+text+"'");
		List<Assignment> assignments = new ArrayList<Assignment>();

		try{
			
			final String MY_QUERY = "SELECT DISTINCT  FROM "+MySQLiteHelper.TABLE_ASSIGNMENT+" A, "+
					MySQLiteHelper.TABLE_SUBJECTS+" B WHERE A."+MySQLiteHelper.ASSIGNMENT_SUBJECTS
					+"=B."+MySQLiteHelper.SUBJECTS_ID
					+" AND (B."+MySQLiteHelper.SUBJECTS_NAME+" LIKE '"+text+"' OR "
					+" B."+MySQLiteHelper.SUBJECTS_ABBREV+" LIKE '"+text+"' OR "
					+" B."+MySQLiteHelper.SUBJECTS_CODE+" LIKE '"+text+"')";

			Cursor cursor = database.rawQuery(MY_QUERY, null);
			
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Assignment data_assignment = cursorToAssignment(cursor);
				assignments.add(data_assignment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			Log.d("Add", "Search results - "+cursor.getCount());
			return assignments;
		}
		catch (Exception e){
			throw e;
		}
		
	}
	
	/**
	 * This method search for assignments with match subjects
	 * @param text
	 * @return list of assignment with match string
	 * @throws Exception 
	 */
	public List<Assignment> searchAssignmentsBySubjects(long Subjects_id) throws Exception{
		
		List<Assignment> assignments = new ArrayList<Assignment>();

		String where =  MySQLiteHelper.ASSIGNMENT_SUBJECTS + " = "+Subjects_id;
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns, where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Assignment data_assignment = cursorToAssignment(cursor);
				assignments.add(data_assignment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			Log.d("Add", "Search results - "+assignments.size());
			return assignments;
		}
		catch (Exception e){
			throw e;
		}
		
	}

	
	/**
	 * This method search for assignments with match date
	 * @param text
	 * @return list of assignment with match string
	 * @throws Exception 
	 */
	public List<Assignment> searchAssignmentsByDate(String date) throws Exception{
		
		List<Assignment> assignments = new ArrayList<Assignment>();

		String where =  MySQLiteHelper.ASSIGNMENT_DUEDATE + " = '"+date+"'";
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns, where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Assignment data_assignment = cursorToAssignment(cursor);
				assignments.add(data_assignment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			Log.d("Add", "Search results - "+assignments.size());
			return assignments;
		}
		catch (Exception e){
			throw e;
		}
		
	}
	
	/**
	 * This method show suggestion for assignments with match title
	 * @param text
	 * @return list of assignment with match string
	 * @throws Exception 
	 */
	public List<Assignment> showSuggestionByTitle(String title) throws Exception{
		
		List<Assignment> assignments = new ArrayList<Assignment>();

		String where =  MySQLiteHelper.ASSIGNMENT_TITLE + " LIKE '" 
				+ title + "%'";
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENT,
					allColumns, where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Assignment data_assignment = cursorToAssignment(cursor);
				assignments.add(data_assignment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			Log.d("Add", "Search results - "+assignments.size());
			return assignments;
		}
		catch (Exception e){
			throw e;
		}
		
	}
	
	/**
	 * Set the alarm to null when cancel or complete
	 * @param assignment_id
	 * @throws Exception
	 */
	public void setAlarmNull(long assignment_id) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
		
			dataToInsert.put(MySQLiteHelper.ASSIGNMENT_ALARM,"");
			
			String where = MySQLiteHelper.ASSIGNMENT_ID + "=" + assignment_id;

			database.update(MySQLiteHelper.TABLE_ASSIGNMENT, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

}
