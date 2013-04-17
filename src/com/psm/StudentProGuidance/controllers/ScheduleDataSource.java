/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class will handles 
 * all the database regarding 
 * table Schedule
 * @author Ng Xin Man
 *
 */
public class ScheduleDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.SCHEDULE_ID,
			MySQLiteHelper.SCHEDULE_INDEX,
			MySQLiteHelper.SCHEDULE_BEGIN,
			MySQLiteHelper.SCHEDULE_END,
			MySQLiteHelper.SCHEDULE_ACADEMIC};

	public ScheduleDataSource(Context context) {
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
	 * Create new Schedule record
	 * @param Schedule
	 * @return newly created object of Schedule
	 */
	public Schedule createSchedule(Schedule schedule) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.SCHEDULE_INDEX, schedule.getIndex());
		values.put(MySQLiteHelper.SCHEDULE_BEGIN, schedule.getBegin());
		values.put(MySQLiteHelper.SCHEDULE_END, schedule.getEnd());
		values.put(MySQLiteHelper.SCHEDULE_ACADEMIC,schedule.getAcademic_id());

		long insertId = database.insert(MySQLiteHelper.TABLE_SCHEDULE, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE,
				allColumns, MySQLiteHelper.SCHEDULE_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Schedule newSchedule = cursorToSchedule(cursor);
		cursor.close();
		return newSchedule;
	}

	/**
	 * Delete Schedule records by Schedule id
	 * @param Schedule
	 */
	public void deleteSchedule(Schedule schedule) {
		long id = schedule.getId();
		System.out.println("Class deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_SCHEDULE, MySQLiteHelper.SCHEDULE_ID
				+ " = " + id, null);
	}
	
	/**
	 * Delete Schedule records by Schedule id
	 * @param Schedule
	 */
	public void deleteSchedule(long academic_id) {
		//long id = schedule.getId();
		//System.out.println("Class deleted with academic id: " + id);
		database.delete(MySQLiteHelper.TABLE_SCHEDULE, MySQLiteHelper.SCHEDULE_ACADEMIC
				+ " = " + academic_id, null);
	}

	/**
	 * Get all the Schedule records by  academic id
	 * @param academic id
	 * @return List of Schedule records
	 */
	public List<Schedule> getAllSchedule(long academic_id) {
		List<Schedule> schedules = new ArrayList<Schedule>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE,
				allColumns, MySQLiteHelper.SCHEDULE_ACADEMIC + " = " + academic_id,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule data_schedule = cursorToSchedule(cursor);
			schedules.add(data_schedule);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return schedules;
	}
	
	/**
	 * Get all the Schedule records by  academic id
	 * @param academic id
	 * @return List of Schedule records
	 * @throws Exception 
	 */
	public int findSchedule(long academic_id) throws Exception {
		try{
		int found = 0;
		Cursor cursor = database.rawQuery
				("SELECT COUNT(*) FROM "+MySQLiteHelper.TABLE_SCHEDULE
						+" WHERE "+MySQLiteHelper.SCHEDULE_ACADEMIC +" = " + academic_id, null );

		cursor.moveToFirst();
		found= cursor.getInt(0);
		// Make sure to close the cursor
		cursor.close();
		
		Log.d("FOUND", String.valueOf(found));
		return found;
		}
		catch(Exception e){
			throw e;
		}
	}

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Schedule object (row)
	 */
	private Schedule cursorToSchedule(Cursor cursor) {
		Schedule thisSchedule = new Schedule();
		thisSchedule.setId(cursor.getLong(0));
		thisSchedule.setIndex(cursor.getInt(1));
		thisSchedule.setBegin(cursor.getString(2));
		thisSchedule.setEnd(cursor.getString(3));
		thisSchedule.setAcademic_id(cursor.getLong(4));
		return thisSchedule;
	}

	/**
	 * Get the details of Schedule by its id
	 * @param Schedule id
	 * @return Schedule object
	 * @throws Exception 
	 */
	public Schedule getCertainSchedule(long id) throws Exception{
		try{
			
			boolean found = false;
			Schedule data_found = new Schedule();
			String where = MySQLiteHelper.SCHEDULE_ID + "=" + id;

			Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Schedule data_schedule = cursorToSchedule(cursor);
				data_found = data_schedule;
			}
		
			// Make sure to close the cursor
			cursor.close();

			if(found == true)
				return data_found;
			else
				return null;

		}
		catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * Upgrade the current record by schedule id
	 * @param schedule
	 * @throws Exception
	 */
	public void updateSchedule(Schedule inSchedule) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.SCHEDULE_INDEX,inSchedule.getIndex());
			dataToInsert.put(MySQLiteHelper.SCHEDULE_BEGIN, inSchedule.getBegin());
			dataToInsert.put(MySQLiteHelper.SCHEDULE_END, inSchedule.getEnd());
			
			String where = MySQLiteHelper.SCHEDULE_ID + "=" + inSchedule.getId();

			database.update(MySQLiteHelper.TABLE_SCHEDULE, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}
}
