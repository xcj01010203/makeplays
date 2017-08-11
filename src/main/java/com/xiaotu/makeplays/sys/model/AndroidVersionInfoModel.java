package com.xiaotu.makeplays.sys.model;

import java.util.Date;

/**
 * 安卓版本信息
 * @author xuchangjian 2017-4-13下午3:49:27
 */
public class AndroidVersionInfoModel {
	
	public static final String TABLE_NAME="tab_android_version_info";
	
	private String id;
	
	/**
	 * 版本号
	 */
	private Integer versionNo;
	
	/**
	 * 版本名称
	 */
	private String versionName;
	
	/**
	 * 更新日志
	 */
	private String updateLog;
	
	/**
	 * 文件大小
	 */
	private long size;
	
	/**
	 * 文件存储路径
	 */
	private String storePath;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersionNo() {
		return this.versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getUpdateLog() {
		return this.updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getStorePath() {
		return this.storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
