package com.xiaotu.makeplays.view.model;

/**
 * 场景地点信息表
 * @author xuchangjian
 */
public class ViewLocationModel {

	/**
	 * 在数据库对应的表名
	 */
	public static final String TABLE_NAME = "tab_view_location";
	
	/**
	 * 场景地点ID
	 */
	private String locationId;
	
	/**
	 * 地点类型，值见枚举类AddressType
	 */
	private int locationType;
	
	/**
	 * 场景地点
	 */
	private String location;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getLocationId() {
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public int getLocationType() {
		return this.locationType;
	}

	public void setLocationType(int locationType) {
		this.locationType = locationType;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

}
