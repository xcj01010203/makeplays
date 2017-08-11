package com.xiaotu.makeplays.scenario.model;

/**
 * 剧本分析分隔符信息表
 * @author xuchangjian
 */
public class SeparatorInfoModel {

	public static final String TABLE_NAME = "tab_separator_info";
	
	
	/**
	 * 分隔符的id
	 */
	private String sepaId;
	
	/**
	 * 分隔符名称
	 */
	private String sepaName;
	
	/**
	 * 分隔符描述
	 */
	private String sepaDesc;
	
	/**
	 * 剧组ID，如果是公用符号，则该值为0，如果是剧组自定义符号，则该值为具体剧组ID
	 */
	private String crewId;
	
	/**
	 * 对应的正则表达式
	 */
	private String regex;

	public String getRegex() {
		return this.regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getSepaId() {
		return this.sepaId;
	}

	public void setSepaId(String sepaId) {
		this.sepaId = sepaId;
	}

	public String getSepaName() {
		return this.sepaName;
	}

	public void setSepaName(String sepaName) {
		this.sepaName = sepaName;
	}

	public String getSepaDesc() {
		return this.sepaDesc;
	}

	public void setSepaDesc(String sepaDesc) {
		this.sepaDesc = sepaDesc;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
