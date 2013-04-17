/**
 * 
 */
package com.psm.StudentProGuidance.entities;

/**
 * @author User
 *
 */
public class Schedule {

	private long id;
	private int index;
	private String begin;
	private String end;
	private long academic_id;
	private int day;
	//For TimeTable Use
	private String text;

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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
	 * @return the schedule_id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param schedule_id the schedule_id to set
	 */
	public void setId(long id) {
		this.id =  id;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * @return the begin
	 */
	public String getBegin() {
		return begin;
	}
	/**
	 * @param begin the begin to set
	 */
	public void setBegin(String begin) {
		this.begin = begin;
	}
	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
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
