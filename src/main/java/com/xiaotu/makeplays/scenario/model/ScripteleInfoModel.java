package com.xiaotu.makeplays.scenario.model;

/**
 * 剧本分析基本元素信息表
 * @author xuchangjian
 */
public class ScripteleInfoModel {

	public static final String TABLE_NAME = "tab_scriptele_info";
	
	
	/**
	 * 元素的id
	 */
	private String eleId;
	
	/**
	 * 元素名称
	 */
	private String eleName;
	
	/**
	 * 元素示例
	 */
	private String eleSample;
	
	/**
	 * 元素对应的正则
	 */
	private String regex;

	public String getEleId() {
		return this.eleId;
	}

	public void setEleId(String eleId) {
		this.eleId = eleId;
	}

	public String getEleName() {
		return this.eleName;
	}

	public void setEleName(String eleName) {
		this.eleName = eleName;
	}

	public String getEleSample() {
		return this.eleSample;
	}

	public void setEleSample(String eleSample) {
		this.eleSample = eleSample;
	}

	public String getRegex() {
		return this.regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
}
