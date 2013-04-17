/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Lecturer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class will handles 
 * all the database regarding 
 * table Lecturer
 * @author Ng Xin Man
 *
 */
public class LecturerDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.LECTURER_ID,
			MySQLiteHelper.LECTURER_NAME,
			MySQLiteHelper.LECTURER_MOBILE,
			MySQLiteHelper.LECTURER_OFFICE,
			MySQLiteHelper.LECTURER_EMAIL,
			MySQLiteHelper.LECTURER_SUBJECTS,
			MySQLiteHelper.LECTURER_ACADEMIC};

	public LecturerDataSource(Context context) {
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
	 * Create new Lecturer record
	 * @param Lecturer
	 * @return newly created object of Lecturer
	 * @throws Exception 
	 */
	public Lecturer createLecturer(Lecturer lecturer) throws Exception {
		try{  
			ContentValues values = new ContentValues();
			
			values.put(MySQLiteHelper.LECTURER_NAME, lecturer.getName());
			if(lecturer.getMobile()!=null){
				values.put(MySQLiteHelper.LECTURER_MOBILE, lecturer.getMobile());
			}else{
				values.putNull(MySQLiteHelper.LECTURER_MOBILE);
			}
			if(lecturer.getMobile()!=null){
				values.put(MySQLiteHelper.LECTURER_OFFICE, lecturer.getOffice());
			}else{
				values.putNull(MySQLiteHelper.LECTURER_OFFICE);
			}
			if(lecturer.getMobile()!=null){
				values.put(MySQLiteHelper.LECTURER_EMAIL, lecturer.getEmail());
			}else{
				values.putNull(MySQLiteHelper.LECTURER_EMAIL);
			}
			values.put(MySQLiteHelper.LECTURER_SUBJECTS, lecturer.getSubjects_id());
			values.put(MySQLiteHelper.LECTURER_ACADEMIC, lecturer.getAcademic_id());

			long newId = database.insert(MySQLiteHelper.TABLE_LECTURER, null,
					values);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_LECTURER,
					allColumns, MySQLiteHelper.LECTURER_ID + " = " + newId, null,
					null, null, null);
			cursor.moveToFirst();
			Lecturer newLecturer = cursorToLecturer(cursor);
			cursor.close();
			return newLecturer;
		}
		catch (Exception e){
			throw e;
		}
	}


	/**
	 * Delete Lecturer records by Lecturer id
	 * @param Lecturer
	 * @throws Exception 
	 */
	public void deleteLecturer(Lecturer lecturer) throws Exception {
		try{  
			long id = lecturer.getId();
			System.out.println("Subjects deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_LECTURER, MySQLiteHelper.LECTURER_ID
					+ " = " + id, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record by Lecturer id
	 * @param academic
	 * @throws Exception
	 */
	public void updateLecturer(Lecturer lecturer) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.LECTURER_NAME, lecturer.getName());
			if(lecturer.getMobile()!=null){
				dataToInsert.put(MySQLiteHelper.LECTURER_MOBILE, lecturer.getMobile());
			}else{
				dataToInsert.putNull(MySQLiteHelper.LECTURER_MOBILE);
			}
			if(lecturer.getMobile()!=null){
				dataToInsert.put(MySQLiteHelper.LECTURER_OFFICE, lecturer.getOffice());
			}else{
				dataToInsert.putNull(MySQLiteHelper.LECTURER_OFFICE);
			}
			if(lecturer.getMobile()!=null){
				dataToInsert.put(MySQLiteHelper.LECTURER_EMAIL, lecturer.getEmail());
			}else{
				dataToInsert.putNull(MySQLiteHelper.LECTURER_EMAIL);
			}
			dataToInsert.put(MySQLiteHelper.LECTURER_SUBJECTS,lecturer.getSubjects_id());

			String where = MySQLiteHelper.LECTURER_ID + "=" + lecturer.getId();

			database.update(MySQLiteHelper.TABLE_LECTURER, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the lecturer records by academic id
	 * @param academic id
	 * @return List of Lecturer records
	 * @throws Exception 
	 */
	public List<Lecturer> getAllLecturer(long academic_id) throws Exception {
		List<Lecturer> lecturer = new ArrayList<Lecturer>();

		Log.d("SubjectDataSource","Get all lecturer");
		Log.d("SubjectDataSource","academic_id"+academic_id);
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_LECTURER,
					allColumns,  MySQLiteHelper.LECTURER_ACADEMIC + " = " + academic_id,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Lecturer data_lecturer = cursorToLecturer(cursor);
				lecturer.add(data_lecturer);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			Log.d("SubjectDataSource","get size "+lecturer.size());
			return lecturer;
		}
		
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Lecturer object (row)
	 */
	private Lecturer cursorToLecturer(Cursor cursor) {
		Lecturer lecturer = new Lecturer();
		lecturer.setId(cursor.getLong(0));
		lecturer.setName(cursor.getString(1));
		lecturer.setMobile(cursor.getString(2));
		lecturer.setOffice(cursor.getString(3));
		lecturer.setEmail(cursor.getString(4));
		lecturer.setSubjects_id(cursor.getLong(5));
		lecturer.setAcademic_id(cursor.getLong(6));
		return lecturer;
	}

	/**
	 * Get the details of Lecturer by its id
	 * @param lecturer that found
	 * @return Lecturer object
	 * @throws Exception 
	 */
	public Lecturer getCertainLecturer(long lecturer_id) throws Exception{
		
		boolean found = false;
		Lecturer data_found = new Lecturer();
		String where = MySQLiteHelper.LECTURER_ID + "="
				+ lecturer_id;

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_LECTURER,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Lecturer data_lecturer = cursorToLecturer(cursor);
				data_found = data_lecturer;
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
