package com.xiaotu.makeplays.notice.model;

import java.util.Date;

/**
 * 通告单信息表
 * @author xuchangjian 2016年8月4日下午4:46:50
 */
public class NoticeInfoModel {

	public static final String TABLE_NAME="tab_notice_info";
	
	/**
	 * 通告单id
	 */
	private String noticeId;
	
	/**
	 * 通告单名称
	*/
	private String noticeName;
	
	/**
	 * 通告日期
	 */
	private Date noticeDate;
	
	/**
	 * 分组id
	 */
	private String groupId;
	
	/**
	 * 销场状态 0：表示未销场;1：表示已销场;详情参见NoticeCanceledStatus枚举类
	 */
	private Integer canceledStatus;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 是否已发布 
	 */
	private boolean published;	
	
	/**
	 * 发布时间
	 */
	private Date publishTime;
	
	public Date getPublishTime() {
		return this.publishTime;
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public boolean getPublished() {
		return this.published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	public String getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	public String getNoticeName() {
		return noticeName;
	}
	public void setNoticeName(String noticeName) {
		this.noticeName = noticeName;
	}
	public Date getNoticeDate() {
		return noticeDate;
	}
	public void setNoticeDate(Date noticeDate) {
		this.noticeDate = noticeDate;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getCanceledStatus() {
		return canceledStatus;
	}
	public void setCanceledStatus(Integer canceledStatus) {
		this.canceledStatus = canceledStatus;
	}
	
	
	
	
	
}
