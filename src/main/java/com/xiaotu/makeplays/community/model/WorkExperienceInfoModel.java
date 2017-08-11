package com.xiaotu.makeplays.community.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 工作经历基本信息表
 * @author wanrenyi 2016年9月1日下午4:28:37
 */
public class WorkExperienceInfoModel{
	
	public static final String TABLE_NAME = "tab_work_experience_info";

	/**
	 * 工作经历id
	 */
	private String experienceId;
	
	/**
	 * 用户id(创建人id)
	 */
	private String createUser;
	
	/**
	 * 剧组名称
	 */
	private String crewName;
	
	/**
	 * 职位id; 多个职位id以"," 进行分隔
	 */
	private String positionId;
	
	/**
	 * 职位名称; 格式：部门-职务 ;多个职位之间以","分隔
	 */
	private String positionName;
	
	/**
	 * 入组时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date  joinCrewDate;
	
	/**
	 * 离组时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date leaveCrewDate;
	
	/**
	 * 工作职责
	 */
	private String workrequirement;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 是否允许修改
	 */
	private String allowUpdate;
	
	public String getAllowUpdate() {
		return allowUpdate;
	}
	public void setAllowUpdate(String allowUpdate) {
		this.allowUpdate = allowUpdate;
	}
	public String getExperienceId() {
		return experienceId;
	}
	public void setExperienceId(String experienceId) {
		this.experienceId = experienceId;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCrewName() {
		return crewName;
	}
	public void setCrewName(String crewName) {
		this.crewName = crewName;
	}
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public Date getJoinCrewDate() {
		return joinCrewDate;
	}
	public void setJoinCrewDate(Date joinCrewDate) {
		this.joinCrewDate = joinCrewDate;
	}
	public Date getLeaveCrewDate() {
		return leaveCrewDate;
	}
	public void setLeaveCrewDate(Date leaveCrewDate) {
		this.leaveCrewDate = leaveCrewDate;
	}
	public String getWorkrequirement() {
		return workrequirement;
	}
	public void setWorkrequirement(String workrequirement) {
		this.workrequirement = workrequirement;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
