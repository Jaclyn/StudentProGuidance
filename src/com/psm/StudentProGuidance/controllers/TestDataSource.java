/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Subjects;
import com.psm.StudentProGuidance.entities.Test;

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
public class TestDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.TEST_ID,
			MySQLiteHelper.TEST_TITLE,
			MySQLiteHelper.TEST_SUBJECTS,
			MySQLiteHelper.TEST_CHAPTER,
			MySQLiteHelper.TEST_VENUE,
			MySQLiteHelper.TEST_DATE,
			MySQLiteHelper.TEST_TIME,
			MySQLiteHelper.TEST_ALARM,
			MySQLiteHelper.TEST_ACADEMIC};

	public TestDataSource(Context context) {
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
	 * Create new Test record
	 * @param Test
	 * @return newly created object of Test
	 * @throws Exception 
	 */
	public Test createTest(Test test) throws Exception {
		try{  
			ContentValues values = new ContentValues();

			values.put(MySQLiteHelper.TEST_TITLE,test.getTitle());
			values.put(MySQLiteHelper.TEST_SUBJECTS, test.getSubjects_id());
			if(test.getChapter().equals("")||test.getChapter()==null){
				values.putNull(MySQLiteHelper.TEST_CHAPTER);
			}else{
				values.put(MySQLiteHelper.TEST_CHAPTER, test.getChapter());
			}
			values.put(MySQLiteHelper.TEST_VENUE,test.getVenue());
			values.put(MySQLiteHelper.TEST_DATE, test.getDate());
			values.put(MySQLiteHelper.TEST_TIME, test.getTime());
			if(test.getAlarm()==null||test.getAlarm().equals("")){
				values.putNull(MySQLiteHelper.TEST_ALARM);
			}else{
				values.put(MySQLiteHelper.TEST_ALARM, test.getAlarm());
			}		
			values.put(MySQLiteHelper.TEST_ACADEMIC,test.getAcademic_id());

			long newId = database.insert(MySQLiteHelper.TABLE_TEST,
					null,values);
			
			Log.d("TestDataSource","newId : "+newId);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_TEST,
					allColumns, MySQLiteHelper.TEST_ID + " = " + newId, null,
					null, null, null);
			cursor.moveToFirst();
			Test newTest = cursorToTest(cursor);
			cursor.close();
			return newTest;
		}
		catch (Exception e){
			throw e;
		}
	}


	/**
	 * Delete Test records by Test id
	 * @param test
	 * @throws Exception 
	 */
	public void deleteAssignment(Test test) throws Exception {
		try{  
			long id = test.getId();
		
			database.delete(MySQLiteHelper.TABLE_TEST, 
					MySQLiteHelper.TEST_ID
					+ " = " + id, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record by test id
	 * @param test 
	 * @throws Exception
	 */
	public void updateTest(Test test) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.TEST_TITLE,test.getTitle());
			dataToInsert.put(MySQLiteHelper.TEST_SUBJECTS, test.getSubjects_id());
			if(test.getChapter()==null||test.getChapter().equals("")){
				dataToInsert.putNull(MySQLiteHelper.TEST_CHAPTER);
			}else{
				dataToInsert.put(MySQLiteHelper.TEST_CHAPTER, test.getChapter());
			}
			dataToInsert.put(MySQLiteHelper.TEST_VENUE, test.getVenue());
			dataToInsert.put(MySQLiteHelper.TEST_DATE, test.getDate());
			dataToInsert.put(MySQLiteHelper.TEST_TIME, test.getTime());
			if(test.getAlarm()==null||test.getAlarm().equals("")){
				dataToInsert.putNull(MySQLiteHelper.TEST_ALARM);
			}else{
				dataToInsert.put(MySQLiteHelper.TEST_ALARM, test.getAlarm());
			}		
			String where = MySQLiteHelper.TEST_ID + "=" + test.getId();

			database.update(MySQLiteHelper.TABLE_TEST, dataToInsert, where, null);
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
	public void updateTestForTurnOffAlarm(long testId) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          

			dataToInsert.putNull(MySQLiteHelper.TEST_ALARM);

			String where = MySQLiteHelper.TEST_ID + "=" + testId;

			database.update(MySQLiteHelper.TABLE_TEST, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the test records by academic id
	 * @param academic id
	 * @return List of test records
	 * @throws Exception 
	 */
	public List<Test> getAllTest(long academic_id) throws Exception {
		List<Test> tests = new ArrayList<Test>();

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_TEST,
					allColumns,  MySQLiteHelper.TEST_ACADEMIC + " = " + academic_id,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Test data_test = cursorToTest(cursor);
				tests.add(data_test);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return tests;
		}
		catch (Exception e){
			throw e;
		}
	}
	
	/**
	 * Get all the test records by academic id
	 * and date
	 * @param academic id
	 * @return List of test records
	 * @throws Exception 
	 */
	public List<Test> getAllRelatedTest(Test inTest) throws Exception {
		List<Test> tests = new ArrayList<Test>();
		
		String where =  MySQLiteHelper.TEST_ACADEMIC + " = " 
				+ inTest.getAcademic_id() + " and "
				+ MySQLiteHelper.TEST_DATE + " = '"
				+ inTest.getDate() + "'";

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_TEST,
					allColumns, where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Test data_test = cursorToTest(cursor);
				tests.add(data_test);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return tests;
		}
		catch (Exception e){
			throw e;
		}
	}
	
	/**
	 * Get all the test records by subjects
	 * (subjects id & academic id)
	 * @param subjects
	 * @return List of test records
	 * @throws Exception 
	 */
	public List<Test> getAllTest(Subjects subjects) throws Exception {
		List<Test> tests = new ArrayList<Test>();
		long academic_id = subjects.getAcademic_id();
		long subjects_id = subjects.getId();
		Log.d("getAllTest", "academic_id : "+academic_id);
		Log.d("getAllTest", "subjects_id : "+subjects_id);
		
		String where = MySQLiteHelper.TEST_ACADEMIC + " = " + academic_id
				+ " AND " + MySQLiteHelper.TEST_SUBJECTS + " = " + subjects_id;

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_TEST,
					allColumns,  where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Test data_test = cursorToTest(cursor);
				tests.add(data_test);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return tests;
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Test object (row)
	 */
	private Test cursorToTest(Cursor cursor) {
		Test test = new Test();
		test.setId(cursor.getLong(0));
		test.setTitle(cursor.getString(1));
		test.setSubjects_id(cursor.getLong(2));
		test.setChapter(cursor.getString(3));
		test.setVenue(cursor.getString(4));
		test.setDate(cursor.getString(5));
		test.setTime(cursor.getString(6));
		test.setAlarm(cursor.getString(7));
		test.setAcademic_id(cursor.getLong(8));
		return test;
	}

	/**
	 * Get the details of Test by its id
	 * @param test that found
	 * @return test object
	 * @throws Exception 
	 */
	public Test getCertainTest(long test_id) throws Exception{

		boolean found = false;
		Test data_found = new Test();
		String where = MySQLiteHelper.TEST_ID + "="
				+ test_id;

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_TEST,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Test data_test = cursorToTest(cursor);
				data_found = data_test;
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
	 * Set the alarm to null when cancel or complete
	 * @param test_id
	 * @throws Exception
	 */
	public void setAlarmNull(long test_id) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
		
			dataToInsert.put(MySQLiteHelper.TEST_ALARM,"");
			
			String where = MySQLiteHelper.TEST_ID + "=" + test_id;

			database.update(MySQLiteHelper.TABLE_TEST, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}
}
