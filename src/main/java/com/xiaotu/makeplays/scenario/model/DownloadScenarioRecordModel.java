package com.xiaotu.makeplays.scenario.model;

/**
 * 下载剧本记录
 * @author xuchangjian 2016-11-10下午5:33:07
 */
public class DownloadScenarioRecordModel {

	public static String TABLE_NAME = "tab_download_scenario_record";
	
	private String id;
	
	private String crewId;
	
	/**
	 * 设备号
	 */
	private String clientUUID;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getClientUUID() {
		return this.clientUUID;
	}

	public void setClientUUID(String clientUUID) {
		this.clientUUID = clientUUID;
	}
	
}
