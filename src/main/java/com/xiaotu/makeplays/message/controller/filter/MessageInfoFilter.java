package com.xiaotu.makeplays.message.controller.filter;

/**
 * @类名：MessageInfoFilter.java
 * @作者：李晓平
 * @时间：2017年4月12日 下午5:03:21
 * @描述：消息过滤条件
 */
public class MessageInfoFilter {
	
	/**
	 * 内容
	 */
	private String content;

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
