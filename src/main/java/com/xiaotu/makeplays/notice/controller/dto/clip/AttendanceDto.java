package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.util.List;

/**
 * 演员出勤信息
 * @author xuchangjian 2015-11-10下午4:52:19
 */
public class AttendanceDto {

	/**
	 * 主要演员出勤信息
	 */
	private List<RoleAttendanceDto> majorRoleAttenInfo;
	
	/**
	 * 特约、群众演员出勤信息
	 */
	private List<RoleAttendanceDto> notMajRoleAttenInfo;

	public List<RoleAttendanceDto> getMajorRoleAttenInfo() {
		return this.majorRoleAttenInfo;
	}

	public void setMajorRoleAttenInfo(List<RoleAttendanceDto> majorRoleAttenInfo) {
		this.majorRoleAttenInfo = majorRoleAttenInfo;
	}

	public List<RoleAttendanceDto> getNotMajRoleAttenInfo() {
		return this.notMajRoleAttenInfo;
	}

	public void setNotMajRoleAttenInfo(List<RoleAttendanceDto> notMajRoleAttenInfo) {
		this.notMajRoleAttenInfo = notMajRoleAttenInfo;
	}
}