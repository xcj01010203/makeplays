package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 部门评分表
 * @author xuchangjian 2016-2-29下午5:59:19
 */
public class DepartmentEvaluateModel {
	
	public static String TABLE_NAME = "tab_department_evaluate";

	private String id;
	
	/**
	 * 部门ID
	 */
	private String departmentId;
	
	/**
	 * 评价人ID
	 */
	private String userId;
	
	/**
	 * 得分
	 */
	private Integer score;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 最后修改时间
	 */
	private Date lastUpdateTime;

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
