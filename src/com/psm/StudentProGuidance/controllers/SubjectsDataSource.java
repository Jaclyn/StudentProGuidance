/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Assignment;
import com.psm.StudentProGuidance.entities.Subjects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class will handles 
 * all the database regarding 
 * table Subjects
 * @author Ng Xin Man
 *
 */
public class SubjectsDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.SUBJECTS_ID,
			MySQLiteHelper.SUBJECTS_NAME,
			MySQLiteHelper.SUBJECTS_CODE,
			MySQLiteHelper.SUBJECTS_ABBREV,
			MySQLiteHelper.SUBJECTS_CREDIT,
			MySQLiteHelper.SUBJECTS_CHAPTER,
			MySQLiteHelper.SUBJECTS_ACADEMIC};

	public SubjectsDataSource(Context context) {
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
	 * Create new Subjects record
	 * @param subjects
	 * @return newly created object of Subjects
	 * @throws Exception 
	 */
	public Subjects createSubjects(Subjects subjects) throws Exception {
		try{  
			ContentValues values = new ContentValues();
			
			values.put(MySQLiteHelper.SUBJECTS_NAME, subjects.getName());
			values.put(MySQLiteHelper.SUBJECTS_CODE, subjects.getCode());
			values.put(MySQLiteHelper.SUBJECTS_ABBREV, subjects.getAbbrev());
			values.put(MySQLiteHelper.SUBJECTS_CREDIT, subjects.getCredit());
			values.put(MySQLiteHelper.SUBJECTS_CHAPTER, subjects.getTotalChapter());
			values.put(MySQLiteHelper.SUBJECTS_ACADEMIC, subjects.getAcademic_id());

			long newId = database.insert(MySQLiteHelper.TABLE_SUBJECTS, null,
					values);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_SUBJECTS,
					allColumns, MySQLiteHelper.SUBJECTS_ID + " = " + newId, null,
					null, null, null);
			cursor.moveToFirst();
			Subjects newSubjects = cursorToSubjects(cursor);
			cursor.close();
			return newSubjects;
		}
		catch (Exception e){
			throw e;
		}
	}


	/**
	 * Delete Subjects records by subjects id
	 * @param subjects
	 * @throws Exception 
	 */
	public void deleteSubjects(Subjects subjects) throws Exception {
		try{  
			long id = subjects.getId();
			System.out.println("Subjects deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_SUBJECTS, MySQLiteHelper.SUBJECTS_ID
					+ " = " + id, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record by Subjects id
	 * @param academic
	 * @throws Exception
	 */
	public void updateSubjects(Subjects subjects) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.SUBJECTS_NAME, subjects.getName());
			dataToInsert.put(MySQLiteHelper.SUBJECTS_CODE, subjects.getCode());
			dataToInsert.put(MySQLiteHelper.SUBJECTS_ABBREV, subjects.getAbbrev());
			dataToInsert.put(MySQLiteHelper.SUBJECTS_CREDIT, subjects.getCredit());
			dataToInsert.put(MySQLiteHelper.SUBJECTS_CHAPTER,subjects.getTotalChapter());

			String where = MySQLiteHelper.SUBJECTS_ID + "=" + subjects.getId();

			database.update(MySQLiteHelper.TABLE_SUBJECTS, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the subjects records by academic id
	 * @param academic id
	 * @return List of Subjects records
	 * @throws Exception 
	 */
	public List<Subjects> getAllSubjects(long academic_id) throws Exception {
		List<Subjects> subjects = new ArrayList<Subjects>();

		Log.d("SubjectDataSource","Get all subjects");
		Log.d("SubjectDataSource","academic_id"+academic_id);
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_SUBJECTS,
					allColumns,  MySQLiteHelper.SUBJECTS_ACADEMIC + " = " + academic_id,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Subjects data_subjects = cursorToSubjects(cursor);
				subjects.add(data_subjects);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			Log.d("SubjectDataSource","get size "+subjects.size());
			return subjects;
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
	/*public List<Subjects> searchSubjectsMatchingText(String text) throws Exception{
		Log.d("Add","Text - '"+text+"'");
		List<Subjects> subjects = new ArrayList<Assignment>();

		String where =  MySQLiteHelper.ASSIGNMENT_TITLE + " LIKE '" 
				+ text + "%'"
				+" OR "
				+	MySQLiteHelper.ass + " LIKE '" 
				+ text + "%'"
				+" OR ";
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
		
	}*/

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Subjects object (row)
	 */
	private Subjects cursorToSubjects(Cursor cursor) {
		Subjects subjects = new Subjects();
		subjects.setId(cursor.getLong(0));
		subjects.setName(cursor.getString(1));
		subjects.setCode(cursor.getString(2));
		subjects.setAbbrev(cursor.getString(3));
		subjects.setCredit(cursor.getInt(4));
		subjects.setTotalChapter(cursor.getInt(5));
		subjects.setAcademic_id(cursor.getLong(6));
		return subjects;
	}

	/**
	 * Get the details of Subjects by its id
	 * @param subjects that found
	 * @return Subjects object
	 * @throws Exception 
	 */
	public Subjects getCertainSubjects(long subject_id) throws Exception{
		
		boolean found = false;
		Subjects data_found = new Subjects();
		String where = MySQLiteHelper.SUBJECTS_ID + "="
				+ subject_id;

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_SUBJECTS,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Subjects data_subjects = cursorToSubjects(cursor);
				data_found = data_subjects;
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

	
}
