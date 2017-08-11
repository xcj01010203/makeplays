package com.xiaotu.makeplays.mobile.server.notice.dto;

/**
 * 通告单中演员角色信息
 * @author xuchangjian
 */
public class NoticeRoleTimeDto {

	/**
	 * 场景角色
	 */
	private String viewRoleName;
	
	/**
	 * 演员名称
	 */
	private String actorName;
	
	/**
	 * 简称
	 */
	private String shortName;
	
	/**
	 * 化妆
	 */
	private String makeup;
	
	/**
	 * 到场
	 */
	private String arriveTime;
	
	/**
	 * 交妆
	 */
	private String giveMakeupTime;
	
	/**
	 * 是否是当前用户关联角色
	 */
	private boolean currentUserRole;

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getViewRoleName() {
		return this.viewRoleName;
	}

	public void setViewRoleName(String viewRoleName) {
		this.viewRoleName = viewRoleName;
	}

	public String getActorName() {
		return this.actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public String getMakeup() {
		return this.makeup;
	}

	public void setMakeup(String makeup) {
		this.makeup = makeup;
	}

	public String getArriveTime() {
		return this.arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getGiveMakeupTime() {
		return this.giveMakeupTime;
	}

	public void setGiveMakeupTime(String giveMakeupTime) {
		this.giveMakeupTime = giveMakeupTime;
	}

	public boolean isCurrentUserRole() {
		return currentUserRole;
	}

	public void setCurrentUserRole(boolean currentUserRole) {
		this.currentUserRole = currentUserRole;
	}
}
