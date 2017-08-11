package com.xiaotu.makeplays.view.model;

import java.util.Date;

/**
 * 剧本的历史版本
 * @author xuchangjian 2015-12-1下午3:40:01
 */
public class HistoryViewContentModel {

	public static final String TABLE_NAME = "tab_history_viewContent";
	
	private String id;
	
	/**
	 * 场景ID
	 */
	private String viewId;
	
	/**
	 * 剧本内容
	 */
	private String content;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 版本
	 */
	private String version;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
