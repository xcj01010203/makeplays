package com.xiaotu.makeplays.community.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 组训中招聘职位基本信息表
 * @author wanrenyi 2016年9月1日下午4:17:01
 */
public class TeamPositionInfoModel implements Serializable{

	public static final String TABLE_NAME = "tab_team_position_info";
	
	/**
	 * 职位id
	 */
	private String positionId;
	
	/**
	 * 组训id
	 */
	private String teamId;
	
	/**
	 * 用户id(创建人id)
	 */
	private String createUser;
	
	/**
	 * 职位名称
	 */
	private String positionName;
	
	/**
	 * 招聘职位id
	 */
	private String needPositionId;
	
	/**
	 * 招聘人数
	 */
	private Integer needPeopleNum;
	
	/**
	 * 职位要求
	 */
	private String positionRequirement;
	
	/**
	 * 组训中招聘职位的状态 1:可用; 2不可用; 默认可用
	 */
	private Integer status = 1;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public String getNeedPositionId() {
		return needPositionId;
	}
	public void setNeedPositionId(String needPositionId) {
		this.needPositionId = needPositionId;
	}
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public Integer getNeedPeopleNum() {
		return needPeopleNum;
	}
	public void setNeedPeopleNum(Integer needPeopleNum) {
		this.needPeopleNum = needPeopleNum;
	}
	public String getPositionRequirement() {
		return positionRequirement;
	}
	public void setPositionRequirement(String positionRequirement) {
		this.positionRequirement = positionRequirement;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
