/**
 * 
 */
package com.psm.StudentProGuidance.entities;

import java.util.List;


/**
 * @author User
 *
 */
public class Summary {
	
	private long academic_id;
	private Academic academic;
	private List<Subjects> subSet;
	private List<Schedule> scheSet;
	private List<Class> classSet;
	private List<Assignment> assignSet;
	private List<Test> testSet;
	
	/**
	 * @return the academic
	 */
	public Academic getAcademic() {
		return academic;
	}
	/**
	 * @param academic the academic to set
	 */
	public void setAcademic(Academic academic) {
		this.academic = academic;
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
	 * @return the subSet
	 */
	public List<Subjects> getSubSet() {
		return subSet;
	}
	/**
	 * @param subSet the subSet to set
	 */
	public void setSubSet(List<Subjects> subSet) {
		this.subSet = subSet;
	}
	/**
	 * @return the scheSet
	 */
	public List<Schedule> getScheSet() {
		return scheSet;
	}
	/**
	 * @param scheSet the scheSet to set
	 */
	public void setScheSet(List<Schedule> scheSet) {
		this.scheSet = scheSet;
	}
	/**
	 * @return the classSet
	 */
	public List<Class> getClassSet() {
		return classSet;
	}
	/**
	 * @param classSet the classSet to set
	 */
	public void setClassSet(List<Class> classSet) {
		this.classSet = classSet;
	}
	/**
	 * @return the assignSet
	 */
	public List<Assignment> getAssignSet() {
		return assignSet;
	}
	/**
	 * @param assignSet the assignSet to set
	 */
	public void setAssignSet(List<Assignment> assignSet) {
		this.assignSet = assignSet;
	}
	/**
	 * @return the testSet
	 */
	public List<Test> getTestSet() {
		return testSet;
	}
	/**
	 * @param testSet the testSet to set
	 */
	public void setTestSet(List<Test> testSet) {
		this.testSet = testSet;
	}


}
