package com.xiaotu.makeplays.shoot.dto;

import java.util.Date;

import com.xiaotu.makeplays.shoot.model.ShootPlanModel;

public class ShootPlanDto {

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
	 * 分组名称
	 */
	private String groupName;
	
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
	
	/**
	 * 拍摄地点
	 */
	private String shootLocations;
	
	/**
	 * 场数
	 */
	private int viewNumTotal;
	
	/**
	 * 页数
	 */
	private double pageCountNumTotal;
	
	/**
	 * 完成率,格式：20.00
	 */
	private double finishRate;

	public ShootPlanDto(ShootPlanModel shootPlan) {
		this.planId = shootPlan.getPlanId();
		this.planName = shootPlan.getPlanName();
		this.startDate = shootPlan.getStartDate();
		this.endDate = shootPlan.getEndDate();
		this.groupId = shootPlan.getGroupId();
		this.updateTime = shootPlan.getUpdateTime();
		this.crewId = shootPlan.getCrewId();
		this.planType = shootPlan.getPlanType();
		this.parentPlan = shootPlan.getParentPlan();
		this.sequence = shootPlan.getSequence();
	}
	
	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

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

	public String getShootLocations() {
		return this.shootLocations;
	}

	public void setShootLocations(String shootLocations) {
		this.shootLocations = shootLocations;
	}

	public int getViewNumTotal() {
		return this.viewNumTotal;
	}

	public void setViewNumTotal(int viewNumTotal) {
		this.viewNumTotal = viewNumTotal;
	}

	public double getPageCountNumTotal() {
		return this.pageCountNumTotal;
	}

	public void setPageCountNumTotal(double pageCountNumTotal) {
		this.pageCountNumTotal = pageCountNumTotal;
	}

	public double getFinishRate() {
		return this.finishRate;
	}

	public void setFinishRate(double finishRate) {
		this.finishRate = finishRate;
	}
}
