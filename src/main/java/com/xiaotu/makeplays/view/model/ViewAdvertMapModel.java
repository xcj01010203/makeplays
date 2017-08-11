package com.xiaotu.makeplays.view.model;

/**
 * 场景和植入广告的关联关系
 * @author xuchangjian
 */
public class ViewAdvertMapModel {
	
	public static final String TABLE_NAME = "tab_view_advert_map";
	
	/**
	 * 关联关系ID
	 */
	private String mapId;
	
	/**
	 * 植入广告ID
	 */
	private String advertId;
	
	/**
	 * 广告类型  1：道具；2：台词；99：其他
	 */
	private String advertType = "99";
	
	/**
	 * 场景ID
	 */
	private String viewId;
	
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

	public String getAdvertId() {
		return this.advertId;
	}

	public void setAdvertId(String advertId) {
		this.advertId = advertId;
	}

	public String getAdvertType() {
		return this.advertType;
	}

	public void setAdvertType(String advertType) {
		this.advertType = advertType;
	}

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
}
