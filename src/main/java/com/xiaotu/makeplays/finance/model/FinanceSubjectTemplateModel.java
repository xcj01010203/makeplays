package com.xiaotu.makeplays.finance.model;

/**
 * 财务科目模板信息表
 * @author xuchangjian 2016-7-27下午6:15:11
 */
public class FinanceSubjectTemplateModel {
	
	public static final String TABLE_NAME="tab_finance_subject_template";
	
	private String id;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 级别，详细信息见FinanceSubjectLevel枚举类
	 */
	private Integer level;
	
	/**
	 * 父科目ID
	 */
	private String parentId;
	
	/**
	 * 类型，详细信息见FinanceSubjTemplType枚举类
	 */
	private Integer type;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
