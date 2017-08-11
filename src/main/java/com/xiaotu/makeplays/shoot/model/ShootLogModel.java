package com.xiaotu.makeplays.shoot.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 剧组拍摄日志
 * @author xuchangjian
 */
public class ShootLogModel {

	public static final String TABLE_NAME = "tab_shoot_log";
	
	/**
	 * 拍摄日志ID
	 */
	private String shootLogId;
	
	/**
	 * 日志记录时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date shootLogTime;
	
	/**
	 * 分组ID
	 */
	private String groupId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 日志信息
	 */
	private String shootLogInfo;
	
	/**
	 * 记录人ID
	 */
	private String userId;
	
	/**
	 * 拍摄地点
	 */
	private String shootLocation;
	
	/**
	 * 出场角色
	 */
	private String shootRole;

	public String getShootLogId() {
		return this.shootLogId;
	}

	public void setShootLogId(String shootLogId) {
		this.shootLogId = shootLogId;
	}

	public Date getShootLogTime() {
		return this.shootLogTime;
	}

	public void setShootLogTime(Date shootLogTime) {
		this.shootLogTime = shootLogTime;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String playId) {
		this.crewId = playId;
	}

	public String getShootLogInfo() {
		return this.shootLogInfo;
	}

	public void setShootLogInfo(String shootLogInfo) {
		this.shootLogInfo = shootLogInfo;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getShootLocation() {
		return this.shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}

	public String getShootRole() {
		return this.shootRole;
	}

	public void setShootRole(String shootRole) {
		this.shootRole = shootRole;
	}
}
