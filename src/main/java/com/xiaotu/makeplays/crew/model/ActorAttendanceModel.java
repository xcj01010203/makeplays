package com.xiaotu.makeplays.crew.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 演员出勤信息表
 * @author lma
 *
 */
public class ActorAttendanceModel {

	public static final String TABLE_NAME="tab_actor_attendance";
	
	
	private String attendanceId;
	
	private String actorId;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date   leaveStartDate;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date  leaveEndDate;
	
	private Integer leaveDays;
	 
	private String leaveReason;
	
	private String crewId;

	public String getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(String attendanceId) {
		this.attendanceId = attendanceId;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public Date getLeaveStartDate() {
		return leaveStartDate;
	}

	public void setLeaveStartDate(Date leaveStartDate) {
		this.leaveStartDate = leaveStartDate;
	}

	public Date getLeaveEndDate() {
		return leaveEndDate;
	}

	public void setLeaveEndDate(Date leaveEndDate) {
		this.leaveEndDate = leaveEndDate;
	}

	public Integer getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(Integer leaveDays) {
		this.leaveDays = leaveDays;
	}

	public String getLeaveReason() {
		return leaveReason;
	}

	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	
	
	
	
	
	
}
