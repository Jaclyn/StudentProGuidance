package com.psm.StudentProGuidance.entities;

/**
 * This class contains all the attributes
 * regards the Assignment table
 * @author Ng Xin Man
 *
 */
public class Assignment {
	
	private long id;
	private String title;
	private long subjects_id;
	private String description;
	private String dueDate;
	private String dueTime;
	private String alarm;
	private long academic_id;
	
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
	/**
	 * @return the subjects_id
	 */
	public long getSubjects_id() {
		return subjects_id;
	}
	/**
	 * @param subjects_id the subjects_id to set
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the dueDate
	 */
	public String getDueDate() {
		return dueDate;
	}
	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	/**
	 * @return the dueTime
	 */
	public String getDueTime() {
		return dueTime;
	}
	/**
	 * @param dueTime the dueTime to set
	 */
	public void setDueTime(String dueTime) {
		this.dueTime = dueTime;
	}
	
}
