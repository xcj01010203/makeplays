package com.xiaotu.makeplays.community.model;

import java.util.Date;

/**
 * 组训与申请职位关联表
 * @author wanrenyi 2016年9月3日下午3:56:51
 */
public class TeamResumeMapModel {

	public static final String TABLE_NAME = "tab_tean_resume_map";
	
	/**
	 * 关联关系id
	 */
	private String mapId;
	
	/**
	 * 组训id
	 */
	private String teamId;
	
	/**
	 * 用户id
	 */
	private String userId;
	
	/**
	 * 招聘的职位id
	 */
	private String positionId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
