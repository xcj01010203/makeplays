package com.xiaotu.makeplays.sys.filter;

public class SyslogFilter {
	
	/**
	 * 剧组名称
	 */
	private String crewName;
	/**
	 * 制片公司
	 */
	private String company;
	/**
	 * 用户姓名
	 */
	private String realName;
	/**
	 * 电话
	 */
	private String phone;
	/**
	 * 操作IP
	 */
	private String userIp;
	/**
	 * 开始时间
	 */
	private String startTime;
	/**
	 * 结束时间
	 */
	private String endTime;
	/**
	 * 日志摘要
	 */
	private String logDesc;
	/**
	 * 剧组ID
	 */
	private String crewId;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 终端类型
	 */
	private String terminal;
	/**
	 * 操作IP，包含、不包含
	 */
	private Integer isIp;
	/**
	 * 操作类型
	 */
	private String operType;
	/**
	 * 操作地点
	 */
	private String address;
	
	/**
	 * 操作对象
	 */
	private String object;
	
	/**
	 * 是否包含内部项目
	 */
	private boolean isIncludeInternalProject;
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getIsIp() {
		return isIp;
	}
	public void setIsIp(Integer isIp) {
		this.isIp = isIp;
	}
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCrewName() {
		return crewName;
	}
	public void setCrewName(String crewName) {
		this.crewName = crewName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getLogDesc() {
		return logDesc;
	}
	public void setLogDesc(String logDesc) {
		this.logDesc = logDesc;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public boolean getIsIncludeInternalProject() {
		return isIncludeInternalProject;
	}
	public void setIsIncludeInternalProject(boolean isIncludeInternalProject) {
		this.isIncludeInternalProject = isIncludeInternalProject;
	}

}
