package com.xiaotu.makeplays.finance.controller.dto;

import java.util.List;

/**
 * 财务科目
 * @author xuchangjian 2016-7-29上午11:15:11
 */
public class FinanceSubjectDto {

	private String id;
	
	/**
	 * 父科目ID
	 */
	private String parentId;

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
	 * 是否有叶子节点
	 */
	private boolean hasChildren;
	
	/**
	 * 子科目
	 */
	private List<FinanceSubjectDto> children;

	public boolean isHasChildren() {
		return this.hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public List<FinanceSubjectDto> getChildren() {
		return this.children;
	}

	public void setChildren(List<FinanceSubjectDto> children) {
		this.children = children;
	}
}
