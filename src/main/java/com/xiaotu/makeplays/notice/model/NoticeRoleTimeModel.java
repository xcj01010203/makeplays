package com.xiaotu.makeplays.notice.model;

import java.util.Date;

/**
 * 演员通告时间表
 * @author xuchangjian
 */
public class NoticeRoleTimeModel {

	public static final String TABLE_NAME="tab_notice_role_time";
	
	private String noticeRoleTimeId;	//ID
	private String giveMakeupTime;	//交妆时间
	private String arriveTime;	//到场时间
	private String viewRoleId;	//角色ID
	private String noticeId;	//通告单ID
	private Date createTime;	//创建时间
	private String makeup;	//化妆
	
	public String getNoticeRoleTimeId() {
		return this.noticeRoleTimeId;
	}
	public void setNoticeRoleTimeId(String noticeRoleTimeId) {
		this.noticeRoleTimeId = noticeRoleTimeId;
	}
	public String getGiveMakeupTime() {
		return this.giveMakeupTime;
	}
	public void setGiveMakeupTime(String giveMakeupTime) {
		this.giveMakeupTime = giveMakeupTime;
	}
	public String getArriveTime() {
		return this.arriveTime;
	}
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	public String getViewRoleId() {
		return this.viewRoleId;
	}
	public void setViewRoleId(String viewRoleId) {
		this.viewRoleId = viewRoleId;
	}
	public String getNoticeId() {
		return this.noticeId;
	}
	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	public Date getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getMakeup() {
		return this.makeup;
	}
	public void setMakeup(String makeup) {
		this.makeup = makeup;
	}
}
