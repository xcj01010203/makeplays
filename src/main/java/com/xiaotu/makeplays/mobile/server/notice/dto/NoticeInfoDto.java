package com.xiaotu.makeplays.mobile.server.notice.dto;

public class NoticeInfoDto {

	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 通告单名称
	 */
	private String noticeName;
	
	/**
	 * 通告单时间，格式：yyyy-MM-dd
	 */
	private String noticeDate;
	
	/**
	 * 版本
	 */
	private String version;
	
	/**
	 * 组别
	 */
	private String groupName;
	
	/**
	 * 发布时间，格式：yyyy-MM-dd HH:mm:ss
	 */
	private String publishTime;
	
	/**
	 * 是否已收取
	 */
	private boolean hasReceived = false;
	
	/**
	 * 销场状态 0：表示未销场;1：表示已销场;详情参见NoticeCanceledStaus枚举类
	 */
	private Integer canceledStatus;
	
	/**
	 * 反馈状态 1：未收取  2：已收取   3：已查看
	 */
	private Integer backStatus;

	public Integer getCanceledStatus() {
		return this.canceledStatus;
	}

	public void setCanceledStatus(Integer canceledStatus) {
		this.canceledStatus = canceledStatus;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getNoticeName() {
		return this.noticeName;
	}

	public void setNoticeName(String noticeName) {
		this.noticeName = noticeName;
	}

	public String getNoticeDate() {
		return this.noticeDate;
	}

	public void setNoticeDate(String noticeDate) {
		this.noticeDate = noticeDate;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getPublishTime() {
		return this.publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public boolean isHasReceived() {
		return this.hasReceived;
	}

	public void setHasReceived(boolean hasReceived) {
		this.hasReceived = hasReceived;
	}

	public Integer getBackStatus() {
		return backStatus;
	}

	public void setBackStatus(Integer backStatus) {
		this.backStatus = backStatus;
	}
}
