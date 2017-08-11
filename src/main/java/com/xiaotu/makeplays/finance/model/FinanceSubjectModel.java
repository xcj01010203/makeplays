package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 财务科目信息表
 * @author xuchangjian 2016-7-27下午6:14:30
 */
public class FinanceSubjectModel {
	
	public static final String TABLE_NAME="tab_finance_subject";
	
	private String id;

	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 模板名称
	 */
	private String name;
	
	/**
	 * 预算科目级别。详细信息见FinanceSubjectLevel枚举类
	 */
	private Integer level;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 排列顺序
	 */
	private Integer sequence;
	
	/**
	 * 父科目ID
	 */
	private String parentId;
	
	/**
	 * 创建
	 */
	private Date createTime;

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSequence() {
		return this.sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
}
