package com.xiaotu.makeplays.community.model;

import java.util.Date;

/**
 * 寻组基本信息表(寻求职位基本信息表)
 * @author wanrenyi 2016年9月1日下午4:38:57
 */
public class SearchTeamInfoModel {

	public static final String TABLE_NAME = "tab_search_team_info";
	
	/**
	 * 寻组信息id
	 */
	private String searchTeamId;
	
	/**
	 * 用户id(创建人id)
	 */
	private String createUser;
	
	/**
	 * 意向职务名称
	 */
	private String likePositionName;
	
	/**
	 * 个人档期开始时间
	 */
	private Date currentStartDate;
	
	/**
	 * 个人档期结束时间
	 */
	private Date currentEndDate;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 意向职位id
	 */
	private String likePositionId;
	
	public String getLikePositionId() {
		return likePositionId;
	}
	public void setLikePositionId(String likePositionId) {
		this.likePositionId = likePositionId;
	}
	public String getSearchTeamId() {
		return searchTeamId;
	}
	public void setSearchTeamId(String searchTeamId) {
		this.searchTeamId = searchTeamId;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getLikePositionName() {
		return likePositionName;
	}
	public void setLikePositionName(String likePositionName) {
		this.likePositionName = likePositionName;
	}
	public Date getCurrentStartDate() {
		return currentStartDate;
	}
	public void setCurrentStartDate(Date currentStartDate) {
		this.currentStartDate = currentStartDate;
	}
	public Date getCurrentEndDate() {
		return currentEndDate;
	}
	public void setCurrentEndDate(Date currentEndDate) {
		this.currentEndDate = currentEndDate;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
