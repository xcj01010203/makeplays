package com.xiaotu.makeplays.notice.model;

import java.util.Date;

import com.xiaotu.makeplays.notice.model.constants.NoticePushFedBackStatus;

/**
 * 通告单反馈信息
 * @author xuchangjian 2015-11-16下午5:52:21
 */
public class NoticePushFedBackModel {

	public static final String 	TABLE_NAME = "tab_notice_pushFedBack";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 通告单版本,此处版本是根据tab_notice_time表中updateTime字段自动生成的一个版本号，格式yyyyMMddHHmmss
	 */
	private String noticeVersion;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 是否需要反馈
	 */
	private boolean needFedBack;
	
	/**
	 * 反馈状态，详情见NoticePushFedBackStatus枚举类
	 */
	private int backStatus = NoticePushFedBackStatus.NotReceived.getValue();
	
	/**
	 * 状态更新时间
	 */
	private Date statusUpdateTime;
	
	/**
	 * 是否满意，详情见NoticeIsSatisfied枚举类
	 */
	private int isSatisfied = 99;
	
	/**
	 * 备注
	 */
	private String remark;

	public boolean getNeedFedBack() {
		return this.needFedBack;
	}

	public void setNeedFedBack(boolean needFedBack) {
		this.needFedBack = needFedBack;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getNoticeVersion() {
		return this.noticeVersion;
	}

	public void setNoticeVersion(String noticeVersion) {
		this.noticeVersion = noticeVersion;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getBackStatus() {
		return this.backStatus;
	}

	public void setBackStatus(int backStatus) {
		this.backStatus = backStatus;
	}

	public Date getStatusUpdateTime() {
		return this.statusUpdateTime;
	}

	public void setStatusUpdateTime(Date statusUpdateTime) {
		this.statusUpdateTime = statusUpdateTime;
	}

	public int getIsSatisfied() {
		return this.isSatisfied;
	}

	public void setIsSatisfied(int isSatisfied) {
		this.isSatisfied = isSatisfied;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
