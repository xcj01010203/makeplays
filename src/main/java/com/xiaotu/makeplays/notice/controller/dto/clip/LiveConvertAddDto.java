package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.text.SimpleDateFormat;

import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;


/**
 * 场记单现场转场信息
 * @author xuchangjian 2015-11-9下午4:31:12
 */
public class LiveConvertAddDto {

	/**
	 * id
	 */
	private String convertId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 转场时间
	 */
	private String convertTime;
	
	/**
	 * 到场时间
	 */
	private String carriveTime;
	
	/**
	 * 开机时间
	 */
	private String cbootTime;
	
	/**
	 * 收工时间
	 */
	private String cpackupTime;
	
	/**
	 * 拍摄地点
	 */
	private String cshootLocation;
	
	/**
	 * 拍摄场景
	 */
	private String cshootScene;
	
	public LiveConvertAddDto() {
		
	}
	
	public LiveConvertAddDto(LiveConvertAddModel liveConvertModel) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.convertId = liveConvertModel.getConvertId();
		this.crewId = liveConvertModel.getCrewId();
		this.noticeId = liveConvertModel.getNoticeId();
		if (liveConvertModel.getConvertTime() != null) {
			this.convertTime = sdf.format(liveConvertModel.getConvertTime());
		}
		if (liveConvertModel.getCarriveTime() != null) {
			this.carriveTime = sdf.format(liveConvertModel.getCarriveTime());
		}
		if (liveConvertModel.getCbootTime() != null) {
			this.cbootTime = sdf.format(liveConvertModel.getCbootTime());
		}
		if (liveConvertModel.getCpackupTime() != null) {
			this.cpackupTime = sdf.format(liveConvertModel.getCpackupTime());
		}
		this.cshootLocation = liveConvertModel.getCshootLocation();
		this.cshootScene  = liveConvertModel.getCshootScene();
	}

	public String getCpackupTime() {
		return this.cpackupTime;
	}

	public void setCpackupTime(String cpackupTime) {
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

	public String getConvertTime() {
		return this.convertTime;
	}

	public void setConvertTime(String convertTime) {
		this.convertTime = convertTime;
	}

	public String getCarriveTime() {
		return this.carriveTime;
	}

	public void setCarriveTime(String carriveTime) {
		this.carriveTime = carriveTime;
	}

	public String getCbootTime() {
		return this.cbootTime;
	}

	public void setCbootTime(String cbootTime) {
		this.cbootTime = cbootTime;
	}
}
