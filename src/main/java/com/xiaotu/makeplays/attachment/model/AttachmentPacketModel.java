package com.xiaotu.makeplays.attachment.model;

import java.util.Date;

/**
 * 附件包表
 * @author xuchangjian 2016-3-1上午9:37:30
 */
public class AttachmentPacketModel {
	
	public static String TABLE_NAME = "tab_attachment_packet";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 是否关联业务
	 */
	private boolean relatedToBuz;
	
	/**
	 * 是否含有附件
	 */
	private boolean containAttment;
	
	/**
	 * 关联的业务类型，详情见AttachmentBuzType
	 */
	private Integer buzType;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean getRelatedToBuz() {
		return this.relatedToBuz;
	}

	public void setRelatedToBuz(boolean relatedToBuz) {
		this.relatedToBuz = relatedToBuz;
	}

	public boolean getContainAttment() {
		return this.containAttment;
	}

	public void setContainAttment(boolean containAttment) {
		this.containAttment = containAttment;
	}

	public Integer getBuzType() {
		return this.buzType;
	}

	public void setBuzType(Integer buzType) {
		this.buzType = buzType;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
