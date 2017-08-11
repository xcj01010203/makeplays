package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 场记单中演员角色出勤信息
 * @author xuchangjian 2015-11-9下午2:51:04
 */
public class RoleAttendanceModel {

	public static final String TABLE_NAME = "tab_roleAttendance_info";
	
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
	 * 操作人ID
	 */
	private String userId;
	
	/**
	 * 演员名称
	 */
	private String actorName;
	
	/**
	 * 角色类型
	 */
	private int viewRoleType;
	
	/**
	 * 角色名称
	 */
	private String viewRoleName;
	
	/**
	 * 角色数量
	 */
	private Integer roleNum = 1;
	
	/**
	 * 到场时间
	 */
	private Date rarriveTime;
	
	/**
	 * 是否迟到
	 */
	private Boolean isLateArrive;
	
	/**
	 * 收工时间
	 */
	private Date rpackupTime;
	
	/**
	 * 是否迟放
	 */
	private Boolean isLatePackup;

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

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getActorName() {
		return this.actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public int getViewRoleType() {
		return this.viewRoleType;
	}

	public void setViewRoleType(int viewRoleType) {
		this.viewRoleType = viewRoleType;
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

	public Date getRarriveTime() {
		return this.rarriveTime;
	}

	public void setRarriveTime(Date rarriveTime) {
		this.rarriveTime = rarriveTime;
	}

	public Boolean getIsLateArrive() {
		return this.isLateArrive;
	}

	public void setIsLateArrive(Boolean isLateArrive) {
		this.isLateArrive = isLateArrive;
	}

	public Date getRpackupTime() {
		return this.rpackupTime;
	}

	public void setRpackupTime(Date rpackupTime) {
		this.rpackupTime = rpackupTime;
	}

	public Boolean getIsLatePackup() {
		return this.isLatePackup;
	}

	public void setIsLatePackup(Boolean isLatePackup) {
		this.isLatePackup = isLatePackup;
	}

}
