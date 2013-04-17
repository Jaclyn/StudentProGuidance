/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Holiday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class will handles 
 * all the database regarding 
 * table Holiday
 * @author Ng Xin Man
 *
 */
public class HolidayDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.HOLIDAY_ID,
			MySQLiteHelper.HOLIDAY_TITLE,
			MySQLiteHelper.HOLIDAY_TYPE,
			MySQLiteHelper.HOLIDAY_STARTDATE,
			MySQLiteHelper.HOLIDAY_ENDDATE,
			MySQLiteHelper.HOLIDAY_ACADEMIC};

	public HolidayDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	/**
	 * Open database connection
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Log.d("Holiday Datasource","Open Sesame");
	}

	/**
	 * Close database connection
	 */
	public void close() {
		dbHelper.close();
		Log.d("Holiday Datasource","Close Sesame");
	}

	/**
	 * Create new Holiday record
	 * @param holiday
	 * @return newly created object of Holiday
	 * @throws Exception 
	 */
	public long createHoliday(Holiday holiday) throws Exception {
		try{
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.HOLIDAY_TITLE, holiday.getTitle());
			values.put(MySQLiteHelper.HOLIDAY_TYPE, holiday.getType());
			values.put(MySQLiteHelper.HOLIDAY_STARTDATE, holiday.getStartdate());
			values.put(MySQLiteHelper.HOLIDAY_ENDDATE, holiday.getEnddate());
			values.put(MySQLiteHelper.HOLIDAY_ACADEMIC, holiday.getAcademic_id());

			long insertId = database.insert(MySQLiteHelper.TABLE_HOLIDAY, 
					null,values);
			
			return insertId;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * Delete Holiday records by academic id
	 * @param holiday
	 * @throws Exception 
	 */
	public void deleteHolidayWithAcademicId(long academic_id) throws Exception {
		
		Log.d("database", "Im deleting holiday by academic id");
		try{
			System.out.println("Holiday deleted with id: " + academic_id);
			database.delete(MySQLiteHelper.TABLE_HOLIDAY, 
					MySQLiteHelper.HOLIDAY_ACADEMIC
					+ " = " + academic_id, null);
		}catch(Exception e){
			throw e;
		}
	}

	/**
	 * Delete Holiday records by holiday id
	 * @param holiday
	 * @throws Exception 
	 */
	public void deleteHoliday(Holiday holiday) throws Exception {
		
		Log.d("database", "Im deleting holiday");
		try{
			long id = holiday.getId();
			System.out.println("Holiday deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_HOLIDAY, 
					MySQLiteHelper.HOLIDAY_ID
					+ " = " + id, null);
		}catch(Exception e){
			throw e;
		}
	}

	/**
	 * Get all the holiday records by  academic id
	 * @param academic id
	 * @return List of Holiday records
	 * @throws Exception 
	 */
	public List<Holiday> getAllHolidays(long academic_id) throws Exception {
		try{
			Log.d("Database", "GetAllHolidays");
			List<Holiday> holidays = new ArrayList<Holiday>();
			String where = MySQLiteHelper.HOLIDAY_ACADEMIC + "="
					+ academic_id;
			
			Cursor cursor = database.query(MySQLiteHelper.TABLE_HOLIDAY,
					allColumns, where,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Holiday data_holiday = cursorToHoliday(cursor);
				holidays.add(data_holiday);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return holidays;
		}catch(Exception e){
			
			Log.e("GetAllHolidays", e.getMessage());
			throw e;
		}
	}

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Holiday object (row)
	 */
	private Holiday cursorToHoliday(Cursor cursor) {
		Holiday holiday = new Holiday();
		holiday.setId(cursor.getLong(0));
		holiday.setTitle(cursor.getString(1));
		holiday.setType(cursor.getInt(2));
		holiday.setStartdate(cursor.getString(3));
		holiday.setEnddate(cursor.getString(4));
		holiday.setAcademic_id(cursor.getLong(5));
		return holiday;
	}

	/**
	 * Get the details of Holiday by its id
	 * @param Holiday that found
	 * @return Holiday object
	 * @throws Exception 
	 */
	public Holiday getCertainHoliday(long holiday_id) throws Exception{

		boolean found = false;
		Holiday data_found = new Holiday();
		String where = MySQLiteHelper.HOLIDAY_ID + "="
				+ holiday_id;

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_HOLIDAY,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Holiday data_holiday = cursorToHoliday(cursor);
				data_found = data_holiday;
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
