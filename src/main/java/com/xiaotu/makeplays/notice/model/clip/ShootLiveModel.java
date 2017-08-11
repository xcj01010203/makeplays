package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 场记单中拍摄现场信息
 * @author xuchangjian 2015-11-9下午2:30:51
 */
public class ShootLiveModel {

	public static final String TABLE_NAME = "tab_shootLive_info";
	
	/**
	 * id
	 */
	private String liveId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 操作人ID
	 */
	private String userId;
	
	/**
	 * 带号
	 */
	private String tapNo;
	
	/**
	 * 拍摄地点
	 */
	private String shootLocation;
	
	/**
	 * 拍摄场景
	 */
	private String shootScene;
	
	/**
	 * 出发时间
	 */
	private Date startTime = new Date();
	
	/**
	 * 到场时间
	 */
	private Date arriveTime;
	
	/**
	 * 开机时间
	 */
	private Date bootTime;
	
	/**
	 * 收工时间
	 */
	private Date packupTime;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 移动端最后保存时间
	 */
	private Date mobileTime;
	
	/**
	 * 服务端最后保存时间
	 */
	private Date serverTime;

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTapNo() {
		return this.tapNo;
	}

	public void setTapNo(String tapNo) {
		this.tapNo = tapNo;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLiveId() {
		return this.liveId;
	}

	public void setLiveId(String liveId) {
		this.liveId = liveId;
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

	public String getShootLocation() {
		return this.shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}

	public String getShootScene() {
		return this.shootScene;
	}

	public void setShootScene(String shootScene) {
		this.shootScene = shootScene;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getArriveTime() {
		return this.arriveTime;
	}

	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
	}

	public Date getBootTime() {
		return this.bootTime;
	}

	public void setBootTime(Date bootTime) {
		this.bootTime = bootTime;
	}

	public Date getPackupTime() {
		return this.packupTime;
	}

	public void setPackupTime(Date packupTime) {
		this.packupTime = packupTime;
	}

	public Date getMobileTime() {
		return this.mobileTime;
	}

	public void setMobileTime(Date mobileTime) {
		this.mobileTime = mobileTime;
	}

	public Date getServerTime() {
		return this.serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}
}
