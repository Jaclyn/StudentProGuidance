/**
 * 
 */
package com.psm.StudentProGuidance.entities;

/**
 * This class contains all the attributes
 * regards the Subjects table
 * @author Ng Xin Man
 *
 */
public class Subjects {

	private long id;
	private String name;
	private String code;
	private String abbrev;
	private int credit;
	private int totalChapter;
	private long academic_id;
	
	/**
	 * @return the abbrev
	 */
	public String getAbbrev() {
		return abbrev;
	}
	/**
	 * @param abbrev the abbrev to set
	 */
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the credit
	 */
	public int getCredit() {
		return credit;
	}
	/**
	 * @param credit the credit to set
	 */
	public void setCredit(int credit) {
		this.credit = credit;
	}
	/**
	 * @return the totalChapter
	 */
	public int getTotalChapter() {
		return totalChapter;
	}
	/**
	 * @param totalChapter the totalChapter to set
	 */
	public void setTotalChapter(int totalChapter) {
		this.totalChapter = totalChapter;
	}


}
