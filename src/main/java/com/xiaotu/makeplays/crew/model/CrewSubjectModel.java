package com.xiaotu.makeplays.crew.model;

/**
 * 剧组题材
 * @author xuchangjian 2016-12-15上午10:17:28
 */
public class CrewSubjectModel {

	public static final String TABLE_NAME = "tab_subject_info";
	
	private String subjectId;
	
	/**
	 * 题材名称
	 */
	private String subjectName;

	public String getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return this.subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
}
