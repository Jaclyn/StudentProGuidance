/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import java.util.ArrayList;
import java.util.List;

import com.psm.StudentProGuidance.entities.Results;

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
public class ResultsDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
		{ MySQLiteHelper.RESULTS_ID,
			MySQLiteHelper.RESULTS_SUBJECTS,
			MySQLiteHelper.RESULTS_TITLE,
			MySQLiteHelper.RESULTS_PERCENT,
			MySQLiteHelper.RESULTS_MARKS_LESS,
			MySQLiteHelper.RESULTS_MARKS_MORE,
			MySQLiteHelper.RESULTS_MARKS,
			MySQLiteHelper.RESULTS_ACADEMIC};

	public ResultsDataSource(Context context) {
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
	 * Create new Results record
	 * @param Results
	 * @return newly created object of Results
	 * @throws Exception 
	 */
	public Results createResults(Results results) throws Exception {
		try{  
			ContentValues values = new ContentValues();

			values.put(MySQLiteHelper.RESULTS_TITLE,results.getTitle());
			values.put(MySQLiteHelper.RESULTS_SUBJECTS, results.getSubjects_id());
			values.put(MySQLiteHelper.RESULTS_PERCENT, results.getPercent());
			values.put(MySQLiteHelper.RESULTS_MARKS_LESS, results.getMarks_S());
			values.put(MySQLiteHelper.RESULTS_MARKS_MORE, results.getMarks_B());
			values.put(MySQLiteHelper.RESULTS_MARKS, results.getMarks());
			values.put(MySQLiteHelper.RESULTS_ACADEMIC,results.getAcademic_id());

			long newId = database.insert(MySQLiteHelper.TABLE_RESULTS,
					null,values);

			Log.d("ResultsDataSource","newId : "+newId);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_RESULTS,
					allColumns, MySQLiteHelper.RESULTS_ID + " = " + newId, null,
					null, null, null);
			cursor.moveToFirst();
			Results newResults = cursorToResults(cursor);
			cursor.close();
			return newResults;
		}
		catch (Exception e){
			Log.e("Results DS", ""+e.getMessage());
			throw e;
		}
	}


	/**
	 * Delete Results records by Results id
	 * @param Results
	 * @throws Exception 
	 */
	public void deleteResults(Results results) throws Exception {
		try{  
			long id = results.getId();

			database.delete(MySQLiteHelper.TABLE_RESULTS, 
					MySQLiteHelper.RESULTS_ID
					+ " = " + id, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Upgrade the current record by results id
	 * @param results
	 * @throws Exception
	 */
	public void updateResults(Results results) throws Exception {

		try{  
			ContentValues dataToInsert = new ContentValues();                          
			dataToInsert.put(MySQLiteHelper.RESULTS_TITLE,results.getTitle());
			dataToInsert.put(MySQLiteHelper.RESULTS_SUBJECTS, results.getSubjects_id());
			dataToInsert.put(MySQLiteHelper.RESULTS_PERCENT, results.getPercent());
			dataToInsert.put(MySQLiteHelper.RESULTS_MARKS_LESS, results.getMarks_S());
			dataToInsert.put(MySQLiteHelper.RESULTS_MARKS_MORE, results.getMarks_B());
			dataToInsert.put(MySQLiteHelper.RESULTS_MARKS, results.getMarks());

			String where = MySQLiteHelper.RESULTS_ID + "=" + results.getId();

			database.update(MySQLiteHelper.TABLE_RESULTS, dataToInsert, where, null);
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the results records by academic id
	 * @param academic id
	 * @return List of results records
	 * @throws Exception 
	 */
	public List<Results> getAllResults(long academic_id) throws Exception {
		List<Results> resultslist = new ArrayList<Results>();

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_RESULTS,
					allColumns,  MySQLiteHelper.RESULTS_ACADEMIC + " = " + academic_id,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Results results = cursorToResults(cursor);
				resultslist.add(results);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return resultslist;
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Get all the results records by results objects
	 * Search by academic id and subjects id
	 * @param academic id
	 * @return List of results records
	 * @throws Exception 
	 */
	public List<Results> getAllResults(Results results) throws Exception {
		List<Results> resultslist = new ArrayList<Results>();
		String where = MySQLiteHelper.RESULTS_ACADEMIC + " = " + results.getAcademic_id()
				+ " and " + MySQLiteHelper.RESULTS_SUBJECTS + " = " + results.getSubjects_id();
		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_RESULTS,
					allColumns, where ,
					null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Results thisResults = cursorToResults(cursor);
				resultslist.add(thisResults);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return resultslist;
		}
		catch (Exception e){
			throw e;
		}
	}

	/**
	 * Cursor that point to every row to the related records.
	 * @param cursor
	 * @return Results object (row)
	 */
	private Results cursorToResults(Cursor cursor) {
		Results results = new Results();
		results.setId(cursor.getLong(0));
		results.setSubjects_id(cursor.getLong(1));
		results.setTitle(cursor.getString(2));
		results.setPercent(cursor.getFloat(3));
		results.setMarks_S(cursor.getFloat(4));
		results.setMarks_B(cursor.getFloat(5));
		results.setMarks(cursor.getFloat(6));
		results.setAcademic_id(cursor.getLong(7));
		return results;
	}

	/**
	 * Get the details of Results by its id
	 * @param Results that found
	 * @return Results object
	 * @throws Exception 
	 */
	public Results getCertainResults(long results_id) throws Exception{

		boolean found = false;
		Results data_found = new Results();
		String where = MySQLiteHelper.RESULTS_ID + "="
				+ results_id;

		try{
			Cursor cursor = database.query(MySQLiteHelper.TABLE_RESULTS,
					allColumns, where, null, null, null, null);

			if(cursor.moveToFirst()){
				found = true;
				Results data_results = cursorToResults(cursor);
				data_found = data_results;
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
