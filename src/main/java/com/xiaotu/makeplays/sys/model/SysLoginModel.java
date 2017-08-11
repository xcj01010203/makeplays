package com.xiaotu.makeplays.sys.model;

public class SysLoginModel {
	
	public static final String TABLE_NAME="tab_user_login_log";
	
	private String logId;
	private String userId;
	private String ip;
	private String address;
	private Integer clientType; //0：PC；1：IOS；2：安卓'
	
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public static String getTableName() {
		return TABLE_NAME;
	}
	public Integer getClientType() {
		return clientType;
	}
	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	

}
