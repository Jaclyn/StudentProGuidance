/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Class;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class will handles 
 * all the database regarding 
 * table Class
 * @author Ng Xin Man
 *
 */
public class ClassDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.CLASS_ID,
			MySQLiteHelper.CLASS_SUBJECTS,
			MySQLiteHelper.CLASS_TITLE,
			MySQLiteHelper.CLASS_DAY,
			MySQLiteHelper.CLASS_TIMEFROM,
			MySQLiteHelper.CLASS_TIMETO,
			MySQLiteHelper.CLASS_ALARM,
			MySQLiteHelper.CLASS_ACADEMIC};

	public ClassDataSource(Context context) {
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
	 * Create new Class record
	 * @param class
	 * @return newly created object of Class
	 * @throws Exception 
	 */
	public Class createClass(Class inClass) throws Exception {
		try{
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.CLASS_SUBJECTS, inClass.getSubjects_id());
			values.put(MySQLiteHelper.CLASS_TITLE, inClass.getTitle());
			values.put(MySQLiteHelper.CLASS_DAY, inClass.getDay());
			values.put(MySQLiteHelper.CLASS_TIMEFROM, inClass.getTime_from());
			values.put(MySQLiteHelper.CLASS_TIMETO, inClass.getTime_to());
			if(inClass.getAlarm()==null||inClass.getAlarm().equals("")){
				values.putNull(MySQLiteHelper.CLASS_ALARM);
			}else{
				values.put(MySQLiteHelper.CLASS_ALARM, inClass.getAlarm());
			}		
			values.put(MySQLiteHelper.CLASS_ACADEMIC, inClass.getAcademic_id());

			long insertId = database.insert(MySQLiteHelper.TABLE_CLASS, null,
					values);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_CLASS,
					allColumns, MySQLiteHelper.CLASS_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			Class newClass = cursorToClass(cursor);
			cursor.close();
			return newClass;
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Delete Class records by class id
	 * @param class
	 * @throws Exception 
	 */
	public void deleteClass(Class inClass) throws Exception {
		try{
			long id = inClass.getId();
			System.out.println("Class deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_CLASS, MySQLiteHelper.CLASS_ID
					+ " = " + id, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the class records by  academic id
	 * @param academic id
	 * @return List of Class records
	 * @throws Exception 
	 */
	public List<Class> getAllClass(long academic_id) throws Exception {
		try{
			List<Class> classes = new ArrayList<Class>();

			Cursor cursor = database.query(MySQLiteHelper.TABLE_CLASS,
					allColumns, MySQLiteHelper.CLASS_ACADEMIC + " = " + academic_id,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Class data_class = cursorToClass(cursor);
				classes.add(data_class);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return classes;
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the class records by  academic id and date
	 * @param academic id
	 * @return List of Class records
	 * @throws Exception 
	 */
	public List<Class> getAllRelatedClass(Class inClass) throws Exception {
		try{
			List<Class> classes = new ArrayList<Class>();

			String where =  MySQLiteHelper.CLASS_ACADEMIC + " = " 
					+ inClass.getAcademic_id() + " and "
					+ MySQLiteHelper.CLASS_DAY + " = '"
					+ inClass.getDay()+"'";

			Cursor cursor = database.query(MySQLiteHelper.TABLE_CLASS,
					allColumns,where,
					null, null, null, null);

			if(cursor.moveToFirst()){
				while (!cursor.isAfterLast()) {
					Class data_class = cursorToClass(cursor);
					classes.add(data_class);
					cursor.moveToNext();
				}
				// Make sure to close the cursor
				cursor.close();
				return classes;
			}else{
				return null;
			}

		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Class object (row)
	 */
	private Class cursorToClass(Cursor cursor) {
		Class thisClass = new Class();
		thisClass.setId(cursor.getLong(0));
		thisClass.setSubjects_id(cursor.getLong(1));
		thisClass.setTitle(cursor.getString(2));
		thisClass.setDay(cursor.getInt(3));
		thisClass.setTime_from(cursor.getLong(4));
		thisClass.setTime_to(cursor.getLong(5));
		thisClass.setAlarm(cursor.getString(6));
		thisClass.setAcademic_id(cursor.getLong(7));
		return thisClass;
	}

	/**
	 * Upgrade the current record by class id
	 * @param class
	 * @throws Exception
	 */
	public void updateClass(Class inClass) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.CLASS_TITLE,inClass.getTitle());
			dataToInsert.put(MySQLiteHelper.CLASS_SUBJECTS, inClass.getSubjects_id());
			dataToInsert.put(MySQLiteHelper.CLASS_DAY, inClass.getDay());
			dataToInsert.put(MySQLiteHelper.CLASS_TIMEFROM, inClass.getTime_from());
			dataToInsert.put(MySQLiteHelper.CLASS_TIMETO, inClass.getTime_to());
			if(inClass.getAlarm()==null||inClass.getAlarm().equals("")){
				dataToInsert.putNull(MySQLiteHelper.CLASS_ALARM);
			}else{
				dataToInsert.put(MySQLiteHelper.CLASS_ALARM, inClass.getAlarm());
			}		
			String where = MySQLiteHelper.CLASS_ID + "=" + inClass.getId();

			database.update(MySQLiteHelper.TABLE_CLASS, dataToInsert, where, null);
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
	public void updateClassForTurnOffAlarm(long classId) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          

			dataToInsert.putNull(MySQLiteHelper.CLASS_ALARM);

			String where = MySQLiteHelper.CLASS_ID + "=" + classId;

			database.update(MySQLiteHelper.TABLE_CLASS, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}


	/**
	 * Get the details of Class by its id
	 * @param class id
	 * @return Class object
	 * @throws Exception 
	 */
	public Class getCertainClass(long id) throws Exception{
		try{

			boolean found = false;
			Class data_found = new Class();
			String where = MySQLiteHelper.CLASS_ID + "=" + id;

			Cursor cursor = database.query(MySQLiteHelper.TABLE_CLASS,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Class data_class = cursorToClass(cursor);
				data_found = data_class;
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
	 * Set the schedule to null after renew schedule
	 * @param class
	 * @throws Exception
	 */
	public void setScheduleZero(long academic_id) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
		
			dataToInsert.put(MySQLiteHelper.CLASS_TIMEFROM,"0");
			dataToInsert.put(MySQLiteHelper.CLASS_TIMETO,"0");
			
			String where = MySQLiteHelper.ACADEMIC_ID + "=" + academic_id;

			database.update(MySQLiteHelper.TABLE_CLASS, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}
	
	/**
	 * Set the alarm to null when cancel or complete
	 * @param class_id
	 * @throws Exception
	 */
	public void setAlarmNull(long class_id) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
		
			dataToInsert.put(MySQLiteHelper.CLASS_ALARM,"");
			
			String where = MySQLiteHelper.CLASS_ID + "=" + class_id;

			database.update(MySQLiteHelper.TABLE_CLASS, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}
	

}
