package com.psm.StudentProGuidance.entities;

/**
 * This class contains all the attributes
 * regards the Academic table
 * @author Ng Xin Man
 *
 */
public class Academic {

	private long id;
	private int semester;
	private String startdate;
	private String enddate;
	private int maxClass;
	private String daySet;
	private float grade_a;
	private float grade_b;
	private float grade_c;
	private float grade_d;
	private String user_id;
	
	/**
	 * @return the daySet
	 */
	public String getDaySet() {
		return daySet;
	}
	/**
	 * @param daySet the daySet to set
	 */
	public void setDaySet(String daySet) {
		this.daySet = daySet;
	}
	
	/**
	 * @return the maxClass
	 */
	public int getMaxClass() {
		return maxClass;
	}
	/**
	 * @param maxClass the maxClass to set
	 */
	public void setMaxClass(int maxClass) {
		this.maxClass = maxClass;
	}
	
	
	/**
	 * @return the grade_a
	 */
	public float getGrade_a() {
		return grade_a;
	}
	/**
	 * @param grade_a the grade_a to set
	 */
	public void setGrade_a(float grade_a) {
		this.grade_a = grade_a;
	}
	/**
	 * @return the grade_b
	 */
	public float getGrade_b() {
		return grade_b;
	}
	/**
	 * @param grade_b the grade_b to set
	 */
	public void setGrade_b(float grade_b) {
		this.grade_b = grade_b;
	}
	/**
	 * @return the grade_c
	 */
	public float getGrade_c() {
		return grade_c;
	}
	/**
	 * @param grade_c the grade_c to set
	 */
	public void setGrade_c(float grade_c) {
		this.grade_c = grade_c;
	}
	/**
	 * @return the grade_d
	 */
	public float getGrade_d() {
		return grade_d;
	}
	/**
	 * @param grade_d the grade_d to set
	 */
	public void setGrade_d(float grade_d) {
		this.grade_d = grade_d;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the semester
	 */
	public int getSemester() {
		return semester;
	}
	/**
	 * @param semester the semester to set
	 */
	public void setSemester(int semester) {
		this.semester = semester;
	}
	/**
	 * @return the startdate
	 */
	public String getStartdate() {
		return startdate;
	}
	/**
	 * @param startdate the startdate to set
	 */
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	/**
	 * @return the enddate
	 */
	public String getEnddate() {
		return enddate;
	}
	/**
	 * @param enddate the enddate to set
	 */
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	/**
	 * @return the holiday_id
	 */
	public String getUser_id() {
		return user_id;
	}
	/**
	 * @param holiday_id the holiday_id to set
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
