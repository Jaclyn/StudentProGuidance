package com.psm.StudentProGuidance.entities;

/**
 * This class contains all the attributes
 * regards the Class table
 * @author Ng Xin Man
 *
 */
public class Class {
	
	private long id;
	private long subjects_id;
	private String title;
	private int day;
	private long time_from;
	private long time_to;
	private String alarm;
	private long academic_id;
	
	/**
	 * @return the time_from
	 */
	public long getTime_from() {
		return time_from;
	}
	/**
	 * @param time_from the time_from to set
	 */
	public void setTime_from(long time_from) {
		this.time_from = time_from;
	}
	/**
	 * @return the time_to
	 */
	public long getTime_to() {
		return time_to;
	}
	/**
	 * @param time_to the time_to to set
	 */
	public void setTime_to(long time_to) {
		this.time_to = time_to;
	}
	/**
	 * @return the alarm
	 */
	public String getAlarm() {
		return alarm;
	}
	/**
	 * @param alarm the alarm to set
	 */
	public void setAlarm(String alarm) {
		this.alarm = alarm;
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
	 * @return the subjects
	 */
	public long getSubjects_id() {
		return subjects_id;
	}
	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjects_id(long subjects_id) {
		this.subjects_id = subjects_id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}
	/**
	 * @return the academic_id
	 */
	public long getAcademic_id() {
		return academic_id;
	}
	/**
	 * @param academic_id the academic_id to set
	 */
	public void setAcademic_id(long academic_id) {
		this.academic_id = academic_id;
	}
	

}
