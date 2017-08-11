package com.xiaotu.makeplays.mobile.common;

public class MobileRequest<T> {

	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 时间戳
	 */
	private String timestamp;
	
	/**
	 * 业务信息
	 */
	private T buzData;

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public T getBuzData() {
		return this.buzData;
	}

	public void setBuzData(T buzData) {
		this.buzData = buzData;
	}
}
