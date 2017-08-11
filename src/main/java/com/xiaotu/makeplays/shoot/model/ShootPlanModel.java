package com.xiaotu.makeplays.shoot.model;

import java.util.Date;

/**
 * 拍摄计划
 * @author xuchangjian
 */
public class ShootPlanModel {

	public static final String TABLE_NAME = "tab_shootplan_info";
	
	/**
	 * 计划ID
	 */
	private String planId;
	
	/**
	 * 计划名称
	 */
	private String planName;
	
	/**
	 * 计划开始日期
	 */
	private Date startDate;
	
	/**
	 * 计划结束日期
	 */
	private Date endDate;
	
	/**
	 * 分组ID
	 */
	private String groupId;
	
	/**
	 * 最近修改时间
	 */
	private Date updateTime;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 计划类型（1：宏观计划   2：详细计划）
	 */
	private int planType;
	
	/**
	 * 上级计划ID
	 */
	private String parentPlan;
	
	/**
	 * 排列顺序
	 */
	private int sequence;

	public String getPlanId() {
		return this.planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getPlanName() {
		return this.planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public int getPlanType() {
		return this.planType;
	}

	public void setPlanType(int planType) {
		this.planType = planType;
	}

	public String getParentPlan() {
		return this.parentPlan;
	}

	public void setParentPlan(String parentPlan) {
		this.parentPlan = parentPlan;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
