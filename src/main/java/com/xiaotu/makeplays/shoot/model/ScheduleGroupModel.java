package com.xiaotu.makeplays.shoot.model;

import java.util.Date;

/**
 * @类名：ScheduleGroupModel.java
 * @作者：李晓平
 * @时间：2017年6月20日 下午2:11:59
 * @描述：计划分组
 */
public class ScheduleGroupModel {

	public static final String TABLE_NAME = "tab_schedule_group";
	
	private String id;
	
	private String groupName;
	
	private Integer sequence;
	
	private Date createTime;
	
	private String crewId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
