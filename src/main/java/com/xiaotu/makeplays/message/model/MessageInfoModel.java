package com.xiaotu.makeplays.message.model;

import java.util.Date;

/**
 * 消息
 * @author xuchangjian 2016-8-12下午7:01:32
 */
public class MessageInfoModel {
	
	public static final String TABLE_NAME="tab_message_info";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 发送消息的人ID
	 */
	private String senderId;
	
	/**
	 * 接收人ID
	 */
	private String receiverId;
	
	/**
	 * 消息类型，详情见MessageType枚举类
	 */
	private Integer type;
	
	/**
	 * 业务ID
	 */
	private String buzId;
	
	/**
	 * 状态，详细信息见MessageInfoStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 提醒日期
	 */
	private Date remindTime;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 是否是新消息
	 */
	private Boolean isNew = true;

	public String getBuzId() {
		return this.buzId;
	}

	public void setBuzId(String buzId) {
		this.buzId = buzId;
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

	public String getSenderId() {
		return this.senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return this.receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getRemindTime() {
		return this.remindTime;
	}

	public void setRemindTime(Date remindTime) {
		this.remindTime = remindTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getIsNew() {
		return this.isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

}
