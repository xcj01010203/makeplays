package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 场记单中现场转场信息
 * @author xuchangjian 2015-11-9下午2:44:37
 */
public class LiveConvertAddModel {

	public static final String TABLE_NAME = "tab_liveConverAdd_info";
	
	/**
	 * id
	 */
	private String convertId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 操作人ID
	 */
	private String userId;
	
	/**
	 * 现场信息ID
	 */
	private String noticeId;
	
	/**
	 * 转场时间
	 */
	private Date convertTime;
	
	/**
	 * 到场时间
	 */
	private Date carriveTime;
	
	/**
	 * 开机时间
	 */
	private Date cbootTime;
	
	/**
	 * 收工时间
	 */
	private Date cpackupTime;
	
	/**
	 * 拍摄地点
	 */
	private String cshootLocation;
	
	/**
	 * 拍摄场景
	 */
	private String cshootScene;

	public Date getCpackupTime() {
		return this.cpackupTime;
	}

	public void setCpackupTime(Date cpackupTime) {
		this.cpackupTime = cpackupTime;
	}

	public String getCshootLocation() {
		return this.cshootLocation;
	}

	public void setCshootLocation(String cshootLocation) {
		this.cshootLocation = cshootLocation;
	}

	public String getCshootScene() {
		return this.cshootScene;
	}

	public void setCshootScene(String cshootScene) {
		this.cshootScene = cshootScene;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getConvertId() {
		return this.convertId;
	}

	public void setConvertId(String convertId) {
		this.convertId = convertId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public Date getConvertTime() {
		return this.convertTime;
	}

	public void setConvertTime(Date convertTime) {
		this.convertTime = convertTime;
	}

	public Date getCarriveTime() {
		return this.carriveTime;
	}

	public void setCarriveTime(Date carriveTime) {
		this.carriveTime = carriveTime;
	}

	public Date getCbootTime() {
		return this.cbootTime;
	}

	public void setCbootTime(Date cbootTime) {
		this.cbootTime = cbootTime;
	}
}
