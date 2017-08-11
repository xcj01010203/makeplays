package com.xiaotu.makeplays.scenario.model;

/**
 * 剧组剧本格式
 * @author xuchangjian 2017-5-9下午1:51:39
 */
public class ScenarioFormatModel {

	public static final String TABLE_NAME = "tab_scenario_format";
	
	private String id;
	
	private String crewId;
	
	/**
	 * 每行显示字数
	 */
	private Integer wordCount = 35;
	
	/**
	 * 每页显示行数
	 */
	private Integer lineCount = 40;
	
	/**
	 * 剧本解析规则
	 */
	private String scriptRule;
	
	/**
	 * 是否支持中文场次
	 */
	private Boolean supportCNViewNo;
	
	/**
	 * 计算页数时是否包含标题
	 */
	private Boolean pageIncludeTitle;

	public Boolean getPageIncludeTitle() {
		return this.pageIncludeTitle;
	}

	public void setPageIncludeTitle(Boolean pageIncludeTitle) {
		this.pageIncludeTitle = pageIncludeTitle;
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

	public Integer getWordCount() {
		return this.wordCount;
	}

	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	public Integer getLineCount() {
		return this.lineCount;
	}

	public void setLineCount(Integer lineCount) {
		this.lineCount = lineCount;
	}

	public String getScriptRule() {
		return this.scriptRule;
	}

	public void setScriptRule(String scriptRule) {
		this.scriptRule = scriptRule;
	}

	public Boolean getSupportCNViewNo() {
		return this.supportCNViewNo;
	}

	public void setSupportCNViewNo(Boolean supportCNViewNo) {
		this.supportCNViewNo = supportCNViewNo;
	}
}
