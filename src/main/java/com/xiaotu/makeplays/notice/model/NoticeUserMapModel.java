package com.xiaotu.makeplays.notice.model;

/**
 * 通告单时间和剧组用户关联关系表
 * @author xuchangjian
 */
public class NoticeUserMapModel {

	public static final String TABLE_NAME = "tab_notice_user_map";
	
	/**
	 * ID
	 */
	private String mapId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单生成信息ID
	 */
	private String noticeTimeId;
	
	/**
	 * 用户ID
	 */
	private String userId;

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getNoticeTimeId() {
		return this.noticeTimeId;
	}

	public void setNoticeTimeId(String noticeTimeId) {
		this.noticeTimeId = noticeTimeId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
