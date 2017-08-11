package com.xiaotu.makeplays.scenario.model;

/**
 * 发布剧本设置
 * @author xuchangjian 2017-8-8下午3:54:58
 */
public class PublishScenarioSettingModel {
	
	public static final String TABLE_NAME = "tab_publish_scenario_setting";

	private String id;
	private String crewId;	//剧组ID
	private String userId;
	private Boolean autoShowPublishWin;	//是否自动显示发布剧本窗口
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
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
	public Boolean getAutoShowPublishWin() {
		return this.autoShowPublishWin;
	}
	public void setAutoShowPublishWin(Boolean autoShowPublishWin) {
		this.autoShowPublishWin = autoShowPublishWin;
	}
}
