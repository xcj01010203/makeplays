package com.xiaotu.makeplays.roleactor.model;

import java.util.Date;

/**
 * 演员基本信息
 * 
 * @author xuchangjian 2016-7-12下午4:31:29
 */
public class ActorInfoModel {

	public static final String TABLE_NAME = "tab_actor_info";

	private String actorId;

	/**
	 * 剧组ID
	 */
	private String crewId;

	/**
	 * 演员姓名
	 */
	private String actorName;

	/**
	 * 入组时间
	 */
	private Date enterDate;

	/**
	 * 离组时间
	 */
	private Date leaveDate;
	
	/**
	 * 在组天数
	 */
	private Integer shootDays;
	
	/**
	 * 工作时长
	 */
	private String workHours;
	
	/**
	 * 休息时长
	 */
	private String restHours;

	public String getWorkHours() {
		return workHours;
	}

	public void setWorkHours(String workHours) {
		this.workHours = workHours;
	}

	public String getRestHours() {
		return restHours;
	}

	public void setRestHours(String restHours) {
		this.restHours = restHours;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public Date getEnterDate() {
		return enterDate;
	}

	public void setEnterDate(Date enterDate) {
		this.enterDate = enterDate;
	}

	public Date getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public Integer getShootDays() {
		return shootDays;
	}

	public void setShootDays(Integer shootDays) {
		this.shootDays = shootDays;
	}

}
