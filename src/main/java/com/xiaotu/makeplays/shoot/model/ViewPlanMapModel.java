package com.xiaotu.makeplays.shoot.model;

import java.util.Date;

/**
 * 场景和拍摄计划关联关系
 * @author xuchangjian
 */
public class ViewPlanMapModel {

	public static final String TABLE_NAME = "tab_view_plan_map";
	
	/**
	 * 关联关系ID
	 */
	private String mapId;
	
	/**
	 * 场景ID
	 */
	private String viewId;
	
	/**
	 * 拍摄计划ID
	 */
	private String planId;
	
	/**
	 * 计划拍摄日期
	 */
	private Date shootDate;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getPlanId() {
		return this.planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Date getShootDate() {
		return this.shootDate;
	}

	public void setShootDate(Date shootDate) {
		this.shootDate = shootDate;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
}
