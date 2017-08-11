package com.xiaotu.makeplays.mobile.server.notice.dto;

/**
 * 部门评分信息
 * @author xuchangjian 2016-3-1上午11:30:06
 */
public class DepartmentEvaluateDto {

	/**
	 * 部门ID
	 */
	private String departmentId;
	
	/**
	 * 部门得分
	 */
	private Integer score;

	public String getDepartmentId() {
		return this.departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
}
