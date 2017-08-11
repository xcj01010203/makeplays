package com.xiaotu.makeplays.roleactor.model;

import java.util.Date;

/**
 * 演员请假记录
 * @author xuchangjian 2016-7-12下午3:43:55
 */
public class ActorLeaveRecordModel {

	public static final String TABLE_NAME = "tab_actor_leave_record";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 演员ID
	 */
	private String actorId;
	
	/**
	 * 开始时间
	 */
	private Date leaveStartDate;
	
	/**
	 * 结束时间
	 */
	private Date leaveEndDate;
	
	/**
	 * 缺勤天数
	 */
	private int leaveDays;
	
	/**
	 * 缺勤原因
	 */
	private String leaveReason;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getActorId() {
		return this.actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public Date getLeaveStartDate() {
		return this.leaveStartDate;
	}

	public void setLeaveStartDate(Date leaveStartDate) {
		this.leaveStartDate = leaveStartDate;
	}

	public Date getLeaveEndDate() {
		return this.leaveEndDate;
	}

	public void setLeaveEndDate(Date leaveEndDate) {
		this.leaveEndDate = leaveEndDate;
	}

	public int getLeaveDays() {
		return this.leaveDays;
	}

	public void setLeaveDays(int leaveDays) {
		this.leaveDays = leaveDays;
	}

	public String getLeaveReason() {
		return this.leaveReason;
	}

	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}
	
}
