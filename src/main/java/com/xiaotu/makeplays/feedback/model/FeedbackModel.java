package com.xiaotu.makeplays.feedback.model;

import java.util.Date;

import com.xiaotu.makeplays.feedback.model.constants.FeedBackStatus;

/**
 * 
 * 用户反馈信息
 * @author Administrator
 *
 */
public class FeedbackModel {
	
	public static final String TABLE_NAME = "tab_feedback_info";
	
	private String id;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 联系方式
	 */
	private String contact;
	
	/**
	 * 反馈意见
	 */
	private String message;
	
	/**
	 * 客户端类型，参考UserClientType枚举类
	 */
	private Integer clientType;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 状态, 参考FeedBackStatus枚举类
	 */
	private Integer status = FeedBackStatus.UnRead.getValue();

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getClientType() {
		return clientType;
	}

	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
