/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Academic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class will handles 
 * all the database regarding 
 * table Academic
 * @author Ng Xin Man
 *
 */
public class AcademicDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private final String TAG = "AcademicDataSource";
	private String[] allColumns =
		{ MySQLiteHelper.ACADEMIC_ID,
			MySQLiteHelper.ACADEMIC_SEM,
			MySQLiteHelper.ACADEMIC_STARTDATE,
			MySQLiteHelper.ACADEMIC_ENDDATE,
			MySQLiteHelper.ACADEMIC_MAXCLASS,
			MySQLiteHelper.ACADEMIC_CLASSDAY,
			MySQLiteHelper.ACADEMIC_A,
			MySQLiteHelper.ACADEMIC_B,
			MySQLiteHelper.ACADEMIC_C,
			MySQLiteHelper.ACADEMIC_D,
			MySQLiteHelper.ACADEMIC_USERID};

	public AcademicDataSource(Context context) {
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
	 * Create new Academic record
	 * @param academic
	 * @return newly created id
	 * @throws Exception 
	 */
	public long createAcademic(Academic academic) throws Exception {
		try{
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.ACADEMIC_SEM, academic.getSemester());
			values.put(MySQLiteHelper.ACADEMIC_STARTDATE, academic.getStartdate());
			values.put(MySQLiteHelper.ACADEMIC_ENDDATE, academic.getEnddate());
			values.putNull(MySQLiteHelper.ACADEMIC_MAXCLASS);
			values.put(MySQLiteHelper.ACADEMIC_CLASSDAY,academic.getDaySet());
			values.putNull(MySQLiteHelper.ACADEMIC_A);
			values.putNull(MySQLiteHelper.ACADEMIC_B);
			values.putNull(MySQLiteHelper.ACADEMIC_C);
			values.putNull(MySQLiteHelper.ACADEMIC_D);
			values.put(MySQLiteHelper.ACADEMIC_USERID, academic.getUser_id());

			long insertId = database.insert(MySQLiteHelper.TABLE_ACADEMIC, null,
					values);

			return insertId;
		}
		catch(Exception e){
			throw e;
		}
	}

	/**
	 * Delete Academic records by academic id
	 * @param academic
	 * @throws Exception 
	 */
	public void deleteAcademicById(long id) throws Exception {
		try{
			System.out.println("Academic deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_ACADEMIC, MySQLiteHelper.ACADEMIC_ID
					+ " = " + id, null);
		}
		catch(Exception e){
			throw e;
		}
	}

	/**
	 * Delete Academic records by academic object
	 * @param academic
	 * @throws Exception 
	 */
	public void deleteAcademic(Academic academic) throws Exception {
		try{
			long id = academic.getId();
			System.out.println("Academic deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_ACADEMIC, MySQLiteHelper.ACADEMIC_ID
					+ " = " + id, null);
		}
		catch(Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record by academic id
	 * @param academic
	 * @throws Exception
	 */
	public void updateAcademic(Academic academic) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.ACADEMIC_SEM, academic.getSemester());
			dataToInsert.put(MySQLiteHelper.ACADEMIC_STARTDATE, academic.getStartdate());
			dataToInsert.put(MySQLiteHelper.ACADEMIC_ENDDATE, academic.getEnddate());
			dataToInsert.put(MySQLiteHelper.ACADEMIC_CLASSDAY,academic.getDaySet());
			String where = MySQLiteHelper.ACADEMIC_ID + "=" + academic.getId();

			database.update(MySQLiteHelper.TABLE_ACADEMIC, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record grade values by academic id
	 * @param academic
	 * @throws Exception
	 */
	public void updateAcademicGrade(Academic academic) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.ACADEMIC_A, academic.getGrade_a());
			dataToInsert.put(MySQLiteHelper.ACADEMIC_B, academic.getGrade_b());
			dataToInsert.put(MySQLiteHelper.ACADEMIC_C, academic.getGrade_c());
			dataToInsert.put(MySQLiteHelper.ACADEMIC_D, academic.getGrade_d());

			String where = MySQLiteHelper.ACADEMIC_ID + "=" + academic.getId();

			database.update(MySQLiteHelper.TABLE_ACADEMIC, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record max class values by academic id
	 * @param academic
	 * @throws Exception
	 */
	public void updateAcademicMaxClass(Academic academic) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.ACADEMIC_MAXCLASS, academic.getMaxClass());

			String where = MySQLiteHelper.ACADEMIC_ID + "=" + academic.getId();

			database.update(MySQLiteHelper.TABLE_ACADEMIC, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the academic records by user id
	 * @param User id
	 * @return List of Academic records
	 * @throws Exception 
	 */
	public List<Academic> getAllAcademics(String userid) throws Exception {
		try{
			List<Academic> academics = new ArrayList<Academic>();

			Cursor cursor = database.query(MySQLiteHelper.TABLE_ACADEMIC,
					allColumns, MySQLiteHelper.ACADEMIC_USERID + " = " + userid,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Academic data_academic = cursorToAcademic(cursor);
				academics.add(data_academic);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return academics;
		}
		catch(Exception e){
			throw e;
		}
	}

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Academic object (row)
	 */
	private Academic cursorToAcademic(Cursor cursor) {
		Academic academic = new Academic();
		academic.setId(cursor.getLong(0));
		academic.setSemester(cursor.getInt(1));
		academic.setStartdate(cursor.getString(2));
		academic.setEnddate(cursor.getString(3));
		academic.setMaxClass(cursor.getInt(4));
		academic.setDaySet(cursor.getString(5));
		academic.setGrade_a(cursor.getInt(6));
		academic.setGrade_b(cursor.getInt(7));
		academic.setGrade_c(cursor.getInt(8));
		academic.setGrade_d(cursor.getInt(9));
		academic.setUser_id(cursor.getString(10));
		return academic;
	}

	/**
	 * Get the details of Academic by its id
	 * @param academic that found
	 * @return Academic object
	 * @throws Exception 
	 */
	public Academic getAcademicByUserID(String userID) throws Exception{
		try{
			String id = userID;
			boolean found = false;
			Academic data_found = new Academic();
			String where = MySQLiteHelper.ACADEMIC_USERID + "=" + id;
			Log.d(TAG, "BEFORE RUN");
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ACADEMIC,
					allColumns, where, null, null, null, null);
			Log.d(TAG, "RUN");
			if(cursor.moveToFirst()){
				Log.d(TAG, "FOUND");
				found = true;
				Academic data_academic = cursorToAcademic(cursor);
				data_found = data_academic;
			}

			Log.d(TAG,"datafound: "+data_found.toString());
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
	 * Get the details of Academic by its id
	 * @param academic that found
	 * @return Academic object
	 * @throws Exception 
	 */
	public Academic getAcademicByID(long id) throws Exception{
		try{

			boolean found = false;
			Academic data_found = new Academic();
			String where = MySQLiteHelper.ACADEMIC_ID + "=" + id;

			Cursor cursor = database.query(MySQLiteHelper.TABLE_ACADEMIC,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Academic data_academic = cursorToAcademic(cursor);
				data_found = data_academic;
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
	 * Verify the semester input by Academic id
	 * @param academic that found
	 * @return Academic object
	 * @throws Exception 
	 */
	public boolean verifySemester(Academic academic) throws Exception{
		try{

			boolean found = false;
			String where = MySQLiteHelper.ACADEMIC_USERID + "=" + academic.getUser_id()
					+ " and " + MySQLiteHelper.ACADEMIC_SEM + "=" + academic.getSemester() ;

			Cursor cursor = database.query(MySQLiteHelper.TABLE_ACADEMIC,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
			}

			// Make sure to close the cursor
			cursor.close();
			return found;

		}
		catch(Exception e){
			throw e;
		}
	}

}
