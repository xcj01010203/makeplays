package com.xiaotu.makeplays.roleactor.model;

import java.util.Date;

/**
 * 演职员评价标签信息表
 * 
 * @author xuchangjian 2016-7-12下午5:33:32
 */
public class EvtagInfoModel {

	public static final String TABLE_NAME = "tab_evtag_info";

	private String tagId;

	/**
	 * 剧组ID
	 */
	private String crewId;

	/**
	 * 标签名称
	 */
	private String tagName;

	/**
	 * 标签类型，详细信息见 EvtagType枚举类
	 */
	private Integer tagType;

	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public Integer getTagType() {
		return tagType;
	}

	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

}
