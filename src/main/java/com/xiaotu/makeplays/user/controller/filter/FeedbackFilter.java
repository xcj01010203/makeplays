package com.xiaotu.makeplays.user.controller.filter;

/**
 * @类名：FeedbackFilter.java
 * @作者：李晓平
 * @时间：2017年4月19日 下午2:24:41
 * @描述：反馈过滤条件
 */
public class FeedbackFilter {
	
	/**
	 * 反馈、回复内容
	 */
	private String content;
	
	/**
	 * 姓名(手机)
	 */
	private String userName;

	/**
	 * 状态，0：未读，1：已读
	 */
	private Integer status;
	
	/**
	 * 开始时间
	 */
	private String startTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
