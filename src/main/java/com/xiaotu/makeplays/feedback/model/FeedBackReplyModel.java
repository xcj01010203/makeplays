package com.xiaotu.makeplays.feedback.model;

import java.util.Date;

import com.xiaotu.makeplays.feedback.model.constants.FeedBackStatus;

/**
 * 
 * 客服回复信息
 * @author Administrator
 *
 */
public class FeedBackReplyModel {
	
	public static final String TABLE_NAME = "tab_feedback_reply";
	
	private String id;
	
	/**
	 * 用户反馈ID
	 */
	private String feedBackId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 回复内容
	 */
	private String reply;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 状态, 参考FeedBackStatus枚举类
	 */
	private Integer status = FeedBackStatus.UnRead.getValue();
	
	/**
	 * 客户端类型，参考UserClientType枚举类
	 */
	private Integer clientType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFeedBackId() {
		return feedBackId;
	}

	public void setFeedBackId(String feedBackId) {
		this.feedBackId = feedBackId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Date getCreateTime() {
		return createTime;
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

	public Integer getClientType() {
		return clientType;
	}

	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	
	
}
