package com.xiaotu.makeplays.shoot.model;

import java.util.Date;

/**
 * @类名：ScheduleViewMapModel.java
 * @作者：李晓平
 * @时间：2017年6月20日 下午2:11:59
 * @描述：计划分组与场景关联关系
 */
public class ScheduleViewMapModel {

	public static final String TABLE_NAME = "tab_view_schedulegroup_map";
	
	private String id;
	
	//场景ID
	private String viewId;
	//计划分组ID
	private String planGroupId;
	//计划拍摄日期
	private Date shootDate;
	//计划拍摄组别,默认单组
	private String shootGroupId = "1";
	//排列顺序
	private Integer sequence;
	//是否锁定
	private Integer isLock;
	//剧组ID
	private String crewId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getPlanGroupId() {
		return planGroupId;
	}

	public void setPlanGroupId(String planGroupId) {
		this.planGroupId = planGroupId;
	}

	public Date getShootDate() {
		return shootDate;
	}

	public void setShootDate(Date shootDate) {
		this.shootDate = shootDate;
	}

	public String getShootGroupId() {
		return shootGroupId;
	}

	public void setShootGroupId(String shootGroupId) {
		this.shootGroupId = shootGroupId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getIsLock() {
		return isLock;
	}

	public void setIsLock(Integer isLock) {
		this.isLock = isLock;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}	
}
