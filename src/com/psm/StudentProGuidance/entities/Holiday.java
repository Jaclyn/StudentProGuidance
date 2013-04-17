/**
 * 
 */
package com.psm.StudentProGuidance.entities;

/**
 * This class contains all the attributes
 * regards the Holiday table
 * @author Ng Xin Man
 *
 */
public class Holiday {
	
	private long id;
	private String title;
	private int type;
	private String startdate;
	private String enddate;
	private long academic_id;
	
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
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
