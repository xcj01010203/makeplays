package com.xiaotu.makeplays.mobile.server.community.filter;

import java.util.Date;

/**
 * 获取寻组信息列表的条件
 * @author wanrenyi 2016年9月6日上午10:26:56
 */
public class SearchTeamFilter {

	/**
	 * 用户id
	 */
	private String userId;
	
	/**
	 * 组训id
	 */
	private String teamId;
	
	/**
	 * 意向职位
	 */
	private String likePositionName;
	
	/**
	 * 最大年龄
	 */
	private Integer maxAge;
	
	/**
	 * 最小年龄
	 */
	private Integer minAge;
	
	/**
	 * 性别 0 女 1 男 3 不限
	 */
	private Integer sex;
	
	/**
	 * 个人档期开始时间
	 */
	private Date currentStartDate;
	
	/**
	 * 个人档期结束时间
	 */
	private Date currentEndDate;
	
	/**
	 * 招聘职位信息
	 */
	private String positionId;
	
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getLikePositionName() {
		return likePositionName;
	}
	public void setLikePositionName(String likePositionName) {
		this.likePositionName = likePositionName;
	}
	public Integer getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	public Integer getMinAge() {
		return minAge;
	}
	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
}
