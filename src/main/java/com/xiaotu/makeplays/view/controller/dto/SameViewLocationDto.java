package com.xiaotu.makeplays.view.controller.dto;

/**
 * 场景地点信息
 * @author wanrenyi 2016年12月12日下午4:12:57
 */
public class SameViewLocationDto {

	/**
	 * 场景id
	 */
	private String viewId;
	
	/**
	 * 场景地点id
	 */
	private String locationId;
	
	/**
	 * 主场景
	 */
	private String mainLocation;
	
	/**
	 * 次级场景
	 */
	private String secondLocation;
	
	/**
	 * 三级场景
	 */
	private String thirdLocation;

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getMainLocation() {
		return mainLocation;
	}

	public void setMainLocation(String mainLocation) {
		this.mainLocation = mainLocation;
	}

	public String getSecondLocation() {
		return secondLocation;
	}

	public void setSecondLocation(String secondLocation) {
		this.secondLocation = secondLocation;
	}

	public String getThirdLocation() {
		return thirdLocation;
	}

	public void setThirdLocation(String thirdLocation) {
		this.thirdLocation = thirdLocation;
	}
	
}
