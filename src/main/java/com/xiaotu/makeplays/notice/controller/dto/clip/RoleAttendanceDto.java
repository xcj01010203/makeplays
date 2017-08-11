package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.text.SimpleDateFormat;

import com.xiaotu.makeplays.notice.model.clip.RoleAttendanceModel;


/**
 * 演员角色出勤信息Dto
 * @author xuchangjian 2015-11-9下午4:28:59
 */
public class RoleAttendanceDto {
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * id
	 */
	private String attendanceId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 演员名称
	 */
	private String actorName;
	
	/**
	 * 角色类型
	 */
	private int roleType;
	
	/**
	 * 角色名称
	 */
	private String viewRoleName;
	
	/**
	 * 演员戏量
	 *//*
	private int viewCount;
	
	*//**
	 * 演员的总数
	 *//*
	private int totalRoleNum;*/
	
	/**
	 * 角色数量
	 */
	private Integer roleNum;
	
	/**
	 * 到场时间
	 */
	private String rarriveTime;
	
	/**
	 * 是否迟到
	 */
	private Boolean isLateArrive;
	
	/**
	 * 收工时间
	 */
	private String rpackupTime;
	
	/**
	 * 是否迟放
	 */
	private Boolean isLatePackup;
	
	public RoleAttendanceDto() {
		
	}
	
	public RoleAttendanceDto(RoleAttendanceModel roleAttendance) {
		this.attendanceId = roleAttendance.getAttendanceId();
		this.crewId = roleAttendance.getCrewId();
		this.noticeId = roleAttendance.getNoticeId();
		this.actorName = roleAttendance.getActorName();
		this.roleType = roleAttendance.getViewRoleType();
		this.viewRoleName = roleAttendance.getViewRoleName();
		this.roleNum = roleAttendance.getRoleNum();
		if (roleAttendance.getRarriveTime() != null) {
			this.rarriveTime = this.sdf.format(roleAttendance.getRarriveTime());
		}
		this.isLateArrive = roleAttendance.getIsLateArrive();
		if (roleAttendance.getRpackupTime() != null) {
			this.rpackupTime = this.sdf.format(roleAttendance.getRpackupTime());
		}
		this.isLatePackup = roleAttendance.getIsLatePackup();
	}

	public String getAttendanceId() {
		return this.attendanceId;
	}

	public void setAttendanceId(String attendanceId) {
		this.attendanceId = attendanceId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getActorName() {
		return this.actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public int getRoleType() {
		return this.roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

	public String getViewRoleName() {
		return this.viewRoleName;
	}

	public void setViewRoleName(String viewRoleName) {
		this.viewRoleName = viewRoleName;
	}

	public Integer getRoleNum() {
		return this.roleNum;
	}

	public void setRoleNum(Integer roleNum) {
		this.roleNum = roleNum;
	}

	public String getRarriveTime() {
		return this.rarriveTime;
	}

	public void setRarriveTime(String rarriveTime) {
		this.rarriveTime = rarriveTime;
	}

	public Boolean getIsLateArrive() {
		return this.isLateArrive;
	}

	public void setIsLateArrive(Boolean isLateArrive) {
		this.isLateArrive = isLateArrive;
	}

	public String getRpackupTime() {
		return this.rpackupTime;
	}

	public void setRpackupTime(String rpackupTime) {
		this.rpackupTime = rpackupTime;
	}

	public Boolean getIsLatePackup() {
		return this.isLatePackup;
	}

	public void setIsLatePackup(Boolean isLatePackup) {
		this.isLatePackup = isLatePackup;
	}

}
