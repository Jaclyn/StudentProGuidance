/**
 * 
 */
package com.psm.StudentProGuidance.controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author User
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

	/** Database details */
	private static final String DATABASE_NAME = "student.db";
	private static final int DATABASE_VERSION = 1;

	/** Table Academic columns details */
	public static final String TABLE_ACADEMIC = "academic";
	public static final String ACADEMIC_ID = "academic_id";
	public static final String ACADEMIC_SEM = "semester";
	public static final String ACADEMIC_STARTDATE = "startdate";
	public static final String ACADEMIC_ENDDATE = "enddate";
	public static final String ACADEMIC_MAXCLASS = "maxclass";
	public static final String ACADEMIC_CLASSDAY = "dayset";
	public static final String ACADEMIC_A = "grade_a";
	public static final String ACADEMIC_B = "grade_b";
	public static final String ACADEMIC_C = "grade_c";
	public static final String ACADEMIC_D = "grade_d";
	public static final String ACADEMIC_USERID = "user_id";

	/** Table Holiday columns details */
	public static final String TABLE_HOLIDAY = "holiday";
	public static final String HOLIDAY_ID = "holiday_id";
	public static final String HOLIDAY_TITLE = "title";
	public static final String HOLIDAY_TYPE = "type";
	public static final String HOLIDAY_STARTDATE = "startdate";
	public static final String HOLIDAY_ENDDATE = "enddate";
	public static final String HOLIDAY_ACADEMIC = "academic_id";

	/** Table Subjects columns details */
	public static final String TABLE_SUBJECTS = "subjects";
	public static final String SUBJECTS_ID = "subjects_id";
	public static final String SUBJECTS_NAME = "name";
	public static final String SUBJECTS_CODE = "code";
	public static final String SUBJECTS_ABBREV = "abbrev";
	public static final String SUBJECTS_CREDIT = "credit";
	public static final String SUBJECTS_CHAPTER = "totalchapter";
	public static final String SUBJECTS_ACADEMIC = "academic_id";

	/** Table Lecturer columns details */
	public static final String TABLE_LECTURER = "lecturer";
	public static final String LECTURER_ID = "lecturer_id";
	public static final String LECTURER_NAME = "name";
	public static final String LECTURER_MOBILE = "mobile";
	public static final String LECTURER_OFFICE = "office";
	public static final String LECTURER_EMAIL = "email";
	public static final String LECTURER_SUBJECTS = "subjects_id";
	public static final String LECTURER_ACADEMIC = "academic_id";

	/** Table Assignment columns details */
	public static final String TABLE_ASSIGNMENT = "assignment";
	public static final String ASSIGNMENT_ID = "assignment_id";
	public static final String ASSIGNMENT_TITLE = "title";
	public static final String ASSIGNMENT_SUBJECTS = "subjects_id";
	public static final String ASSIGNMENT_DESCRIPTION = "description";
	public static final String ASSIGNMENT_DUEDATE = "duedate";
	public static final String ASSIGNMENT_DUETIME = "duetime";
	public static final String ASSIGNMENT_ALARM = "alarm";
	public static final String ASSIGNMENT_ACADEMIC = "academic_id";

	/** Table Test columns details */
	public static final String TABLE_TEST = "test";
	public static final String TEST_ID = "test_id";
	public static final String TEST_TITLE = "title";
	public static final String TEST_SUBJECTS = "subjects_id";
	public static final String TEST_CHAPTER = "chapter";
	public static final String TEST_VENUE = "venue";
	public static final String TEST_DATE = "date";
	public static final String TEST_TIME = "time";
	public static final String TEST_ALARM = "alarm";
	public static final String TEST_ACADEMIC = "academic_id";

	/** Table Class columns details */
	public static final String TABLE_CLASS = "class";
	public static final String CLASS_ID = "class_id";
	public static final String CLASS_SUBJECTS = "subjects_id";
	public static final String CLASS_TITLE = "title";
	public static final String CLASS_DAY = "day";
	public static final String CLASS_TIMEFROM = "from_time";
	public static final String CLASS_TIMETO = "to_time";
	public static final String CLASS_ALARM = "alarm";	
	public static final String CLASS_ACADEMIC = "academic_id";
	
	/** Table Schedule columns details */
	public static final String TABLE_SCHEDULE = "schedule";
	public static final String SCHEDULE_ID = "schedule_id";
	public static final String SCHEDULE_INDEX = "index_no";
	public static final String SCHEDULE_BEGIN = "begin";
	public static final String SCHEDULE_END = "end";
	public static final String SCHEDULE_ACADEMIC = "academic_id";
	
	/** Table Results columns details */
	public static final String TABLE_RESULTS = "results";
	public static final String RESULTS_ID = "results_id";
	public static final String RESULTS_SUBJECTS = "subjects_id";
	public static final String RESULTS_TITLE = "title";
	public static final String RESULTS_PERCENT = "percent";
	public static final String RESULTS_MARKS_LESS = "marks_less";
	public static final String RESULTS_MARKS_MORE = "marks_more";
	public static final String RESULTS_MARKS = "marks";
	public static final String RESULTS_ACADEMIC = "academic_id";
	
	/** Table creation sql statement */
	/****************************
	 * ACADEMIC_CLASSDAY
	 * ================
	 * RANGE OF WEEKDAYS
	 * 1,2,3,4,5,6,7
	 * 
	 * CLASS_DAY (according to SDK)
	 * =========
	 * SUNDAY=1
	 * MONDAY=2
	 * TUESDAY=3
	 * WEDNESDAY=4
	 * THURSDAY=5
	 * FRIDAY=6
	 * SATURDAY=7
	 * 
	 * HOLIDAY TYPE
	 * ===========
	 * ONE DATE=0
	 * DURATION=1
	 ****************************/

	private static final String ACADEMIC_CREATE = "create table "
			+ TABLE_ACADEMIC + "( " + ACADEMIC_ID
			+ " integer primary key autoincrement, " 
			+ ACADEMIC_SEM + " integer not null," 
			+ ACADEMIC_STARTDATE + " text not null," 
			+ ACADEMIC_ENDDATE + " text not null,"
			+ ACADEMIC_MAXCLASS + " integer,"
			+ ACADEMIC_CLASSDAY + " text,"
			+ ACADEMIC_A + " real,"
			+ ACADEMIC_B + " real,"
			+ ACADEMIC_C + " real,"
			+ ACADEMIC_D + " real,"
			+ ACADEMIC_USERID + " text not null " 
			+ ")";

	private static final String HOLIDAY_CREATE = "create table "
			+ TABLE_HOLIDAY + "( " + HOLIDAY_ID
			+ " integer primary key autoincrement, " 
			+ HOLIDAY_TITLE	+ " text not null," 
			+ HOLIDAY_TYPE + " integer not null," 
			+ HOLIDAY_STARTDATE + " text not null," 
			+ HOLIDAY_ENDDATE + " text not null," 
			+ HOLIDAY_ACADEMIC + " integer not null " 
			+ ")";

	private static final String SUBJECTS_CREATE = "create table "
			+ TABLE_SUBJECTS + "( " + SUBJECTS_ID
			+ " integer primary key autoincrement, " 
			+ SUBJECTS_CODE + " text not null," 
			+ SUBJECTS_NAME	+ " text not null," 
			+ SUBJECTS_ABBREV + " text not null," 
			+ SUBJECTS_CREDIT + " integer," 
			+ SUBJECTS_CHAPTER + " integer," 
			+ SUBJECTS_ACADEMIC + " integer not null "
			+ ")";

	private static final String LECTURER_CREATE = "create table "
			+ TABLE_LECTURER + "( " + LECTURER_ID
			+ " integer primary key autoincrement, " 
			+ LECTURER_NAME + " text not null," 
			+ LECTURER_MOBILE	+ " text," 
			+ LECTURER_OFFICE + " text," 
			+ LECTURER_EMAIL + " text," 
			+ LECTURER_SUBJECTS + " integer not null," 
			+ LECTURER_ACADEMIC + " integer not null "
			+ ")";

	private static final String ASSIGNMENT_CREATE = "create table "
			+ TABLE_ASSIGNMENT + "( " + ASSIGNMENT_ID
			+ " integer primary key autoincrement, " 
			+ ASSIGNMENT_TITLE	+ " text not null," 
			+ ASSIGNMENT_SUBJECTS + " integer not null," 
			+ ASSIGNMENT_DESCRIPTION + " text," 
			+ ASSIGNMENT_DUEDATE + " text not null," 
			+ ASSIGNMENT_DUETIME + " text not null," 
			+ ASSIGNMENT_ALARM + " text," 
			+ ASSIGNMENT_ACADEMIC + " integer not null"
			+ ")";

	private static final String TEST_CREATE = "create table "
			+ TABLE_TEST + "( " + TEST_ID
			+ " integer primary key autoincrement, " 
			+ TEST_TITLE	+ " text not null," 
			+ TEST_SUBJECTS + " integer not null," 
			+ TEST_CHAPTER + " text," 
			+ TEST_VENUE + " text not null," 
			+ TEST_DATE + " text not null," 
			+ TEST_TIME + " text not null," 
			+ TEST_ALARM + " text," 
			+ TEST_ACADEMIC + " integer not null"
			+ ")";

	
	private static final String CLASS_CREATE = "create table "
			+ TABLE_CLASS + "( " + CLASS_ID
			+ " integer primary key autoincrement, " 
			+ CLASS_SUBJECTS + " integer not null,"
			+ CLASS_TITLE + " text not null," 
			+ CLASS_DAY + " integer not null," 
			+ CLASS_TIMEFROM + " text not null," 
			+ CLASS_TIMETO + " text not null," 
			+ CLASS_ALARM + " text,"
			+ CLASS_ACADEMIC + " integer not null " 
			+ ")";
	
	private static final String SCHEDULE_CREATE = "create table "
			+ TABLE_SCHEDULE + "( " + SCHEDULE_ID
			+ " integer primary key autoincrement, " 
			+ SCHEDULE_INDEX + " integer not null,"
			+ SCHEDULE_BEGIN + " text not null," 
			+ SCHEDULE_END + " text not null,"
			+ SCHEDULE_ACADEMIC + " integer not null " 
			+ ")";
	
	private static final String RESULTS_CREATE = "create table "
			+ TABLE_RESULTS + "( " + RESULTS_ID
			+ " integer primary key autoincrement, " 
			+ RESULTS_SUBJECTS + " integer not null,"
			+ RESULTS_TITLE + " text not null," 
			+ RESULTS_PERCENT + " real not null," 
			+ RESULTS_MARKS_LESS + " real not null,"
			+ RESULTS_MARKS_MORE + " real not null,"
			+ RESULTS_MARKS + " real not null," 
			+ RESULTS_ACADEMIC + " integer not null " 
			+ ")";


	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(ACADEMIC_CREATE);
		Log.d(DATABASE_NAME, "Academic created!	");
		database.execSQL(HOLIDAY_CREATE);
		database.execSQL(SUBJECTS_CREATE);
		database.execSQL(LECTURER_CREATE);
		database.execSQL(ASSIGNMENT_CREATE);
		database.execSQL(TEST_CREATE);
		database.execSQL(CLASS_CREATE);
		database.execSQL(SCHEDULE_CREATE);
		database.execSQL(RESULTS_CREATE);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACADEMIC);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOLIDAY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
		onCreate(db);
	}

}
