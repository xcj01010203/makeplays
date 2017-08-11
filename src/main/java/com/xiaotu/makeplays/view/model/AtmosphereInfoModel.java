package com.xiaotu.makeplays.view.model;

/**
 * 场景气氛基本信息表
 * @author xuchangjian
 */
public class AtmosphereInfoModel {

	public static final String TABLE_NAME = "tab_atmosphere_info";
	
	/**
	 * 气氛ID
	 */
	private String atmosphereId;
	
	/**
	 * 场景气氛名称
	 */
	private String atmosphereName;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getAtmosphereId() {
		return this.atmosphereId;
	}

	public void setAtmosphereId(String atmosphereId) {
		this.atmosphereId = atmosphereId;
	}

	public String getAtmosphereName() {
		return this.atmosphereName;
	}

	public void setAtmosphereName(String atmosphereName) {
		this.atmosphereName = atmosphereName;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

}
