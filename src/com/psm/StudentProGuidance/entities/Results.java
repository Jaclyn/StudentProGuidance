/**
 * 
 */
package com.psm.StudentProGuidance.entities;

/**
 * @author User
 *
 */
public class Results {
	
	private long id;
	private long subjects_id;
	private String title;
	private float percent;
	private float marks_S;
	private float marks_B;
	private float marks;
	private long academic_id;
	
	/**
	 * @return the marks_S
	 */
	public float getMarks_S() {
		return marks_S;
	}
	/**
	 * @param marks_S the marks_S to set
	 */
	public void setMarks_S(float marks_S) {
		this.marks_S = marks_S;
	}
	/**
	 * @return the marks_B
	 */
	public float getMarks_B() {
		return marks_B;
	}
	/**
	 * @param marks_B the marks_B to set
	 */
	public void setMarks_B(float marks_B) {
		this.marks_B = marks_B;
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
	 * @return the percent
	 */
	public float getPercent() {
		return percent;
	}
	/**
	 * @param percent the percent to set
	 */
	public void setPercent(float percent) {
		this.percent = percent;
	}
	/**
	 * @return the marks
	 */
	public float getMarks() {
		return marks;
	}
	/**
	 * @param marks the marks to set
	 */
	public void setMarks(float marks) {
		this.marks = marks;
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
