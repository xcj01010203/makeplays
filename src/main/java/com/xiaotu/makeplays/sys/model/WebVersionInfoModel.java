package com.xiaotu.makeplays.sys.model;

import java.util.Date;

/**
 * @类名：WebVersionInfoModel.java
 * @作者：李晓平
 * @时间：2017年6月14日 上午11:24:45
 * @描述：web版本信息
 */
public class WebVersionInfoModel {
	
	public static final String TABLE_NAME="tab_web_version_info";
	
	private String id;
	
	/**
	 * 版本名称
	 */
	private String versionName;
	
	/**
	 * 内部更新日志
	 */
	private String insideUpdateLog;
	
	/**
	 * 用户更新日志
	 */
	private String userUpdateLog;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getInsideUpdateLog() {
		return insideUpdateLog;
	}

	public void setInsideUpdateLog(String insideUpdateLog) {
		this.insideUpdateLog = insideUpdateLog;
	}

	public String getUserUpdateLog() {
		return userUpdateLog;
	}

	public void setUserUpdateLog(String userUpdateLog) {
		this.userUpdateLog = userUpdateLog;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
