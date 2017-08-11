package com.xiaotu.makeplays.bulletin.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 剧组消息公告信息表
 * @author xuchangjian
 */
public class BulletinInfoModel {

	public static final String TABLE_NAME = "tab_bulletin_info";
	
	/**
	 * 公告ID
	 */
	private String bulletinId;
	
	/**
	 * 公告名称
	 */
	private String bulletinName;
	
	/**
	 * 公告内容
	 */
	private String content;
	
	/**
	 * 附件地址
	 */
	private String attachUrl;
	
	/**
	 * 附件名称
	 */
	private String attachName;
	
	/**
	 * 公告发布人ID
	 */
	private String pubUserId;
	
	/**
	 * 公告发布人名称
	 */
	private String pubUserName;
	
	/**
	 * 工单创建时间
	 */
	private Date createTime;
	
	/**
	 * 公告有效开始时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;
	
	/**
	 * 公告有效结束时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	
	/**
	 * 公告状态，0：草稿；1：发布；2：撤回
	 */
	private int status;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getPubUserId() {
		return this.pubUserId;
	}

	public void setPubUserId(String pubUserId) {
		this.pubUserId = pubUserId;
	}

	public String getBulletinId() {
		return this.bulletinId;
	}

	public void setBulletinId(String bulletinId) {
		this.bulletinId = bulletinId;
	}

	public String getBulletinName() {
		return this.bulletinName;
	}

	public void setBulletinName(String bulletinName) {
		this.bulletinName = bulletinName;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttachUrl() {
		return this.attachUrl;
	}

	public void setAttachUrl(String attachUrl) {
		this.attachUrl = attachUrl;
	}

	public String getPubUserName() {
		return this.pubUserName;
	}

	public void setPubUserName(String pubUserName) {
		this.pubUserName = pubUserName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getAttachName() {
		return this.attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}
}
