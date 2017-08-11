package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 机位信息
 * @author xuchangjian 2015-11-9下午5:04:28
 */
public class CameraInfoModel {

	public static final String TABLE_NAME = "tab_camera_info";
	
	/**
	 * id
	 */
	private String cameraId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 添加人ID
	 */
	private String userId;
	
	/**
	 * 机位名称
	 */
	private String cameraName;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCameraId() {
		return this.cameraId;
	}

	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getCameraName() {
		return this.cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
