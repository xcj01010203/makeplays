package com.xiaotu.makeplays.view.model;

/**
 * 场景与场景地点关联信息表
 * @author xuchangjian
 */
public class ViewLocationMapModel {

	public static final String 	TABLE_NAME = "tab_view_location_map";
	
	/**
	 * 关联关系ID
	 */
	private String mapId;
	
	/**
	 * 场景ID
	 */
	private String viewId;
	
	/**
	 * 场景地点ID
	 */
	private String locationId;
	
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

	public String getLocationId() {
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
