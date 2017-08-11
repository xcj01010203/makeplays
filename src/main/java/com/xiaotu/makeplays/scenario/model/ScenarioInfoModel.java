package com.xiaotu.makeplays.scenario.model;

import java.util.Date;

/**
 * 剧本基本信息
 * @author xuchangjian
 */
public class ScenarioInfoModel {

	/**
	 * 对应的数据库表名
	 */
	public static final String TABLE_NAME="tab_scenario_info";
	
	/**
	 * 剧本ID
	 */
	private String scenarioId;
	
	/**
	 * 剧本名称
	 */
	private String scenarioName;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 剧本在服务器上的存储地址
	 */
	private String scenarioUrl;
	
	/**
	 * 上传时间
	 */
	private Date uploadTime;
	
	/**
	 * 上传人id
	 */
	private String userId;
	
	/**
	 * 一行字数
	 */
	private int wordCount;
	
	/**
	 * 一页行数
	 */
	private int lineCount;
	
	/**
	 * 剧本解析状态
	 */
	private boolean status = true;
	
	/**
	 * 上传结果描述
	 */
	private String uploadDesc;
	
	/**
	 * 剧本分析规则
	 */
	private String scriptRule;
	
	/**
	 * 是否支持中文的场次解析
	 */
	private boolean supportCNViewNo;
	
	public boolean getSupportCNViewNo() {
		return this.supportCNViewNo;
	}

	public void setSupportCNViewNo(boolean supportCNViewNo) {
		this.supportCNViewNo = supportCNViewNo;
	}

	public String getScriptRule() {
		return this.scriptRule;
	}

	public void setScriptRule(String scriptRule) {
		this.scriptRule = scriptRule;
	}

	public boolean getStatus() {
		return this.status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getUploadDesc() {
		return this.uploadDesc;
	}

	public void setUploadDesc(String uploadDesc) {
		this.uploadDesc = uploadDesc;
	}

	public String getScenarioId() {
		return this.scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getScenarioName() {
		return this.scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getScenarioUrl() {
		return this.scenarioUrl;
	}

	public void setScenarioUrl(String scenarioUrl) {
		this.scenarioUrl = scenarioUrl;
	}

	public Date getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getWordCount() {
		return this.wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getLineCount() {
		return this.lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
}
