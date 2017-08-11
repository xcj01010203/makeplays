package com.xiaotu.makeplays.view.model;

/**
 * 植入广告基本信息表
 * @author xuchangjian
 */
public class InsideAdvertModel {

	public static final String TABLE_NAME = "tab_inside_advert";
	
	/**
	 * 广告ID
	 */
	private String advertId;
	
	/**
	 * 广告名称
	 */
	private String advertName;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getAdvertId() {
		return this.advertId;
	}

	public void setAdvertId(String advertId) {
		this.advertId = advertId;
	}

	public String getAdvertName() {
		return this.advertName;
	}

	public void setAdvertName(String advertName) {
		this.advertName = advertName;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
