/**
 * 
 */
package com.psm.StudentProGuidance.entities;

/**
 * @author User
 *
 */
public class Lecturer {

	private long id;
	private String name;
	private String mobile;
	private String office;
	private String email;
	private long subjects_id;
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
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return the office
	 */
	public String getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(String office) {
		this.office = office;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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
