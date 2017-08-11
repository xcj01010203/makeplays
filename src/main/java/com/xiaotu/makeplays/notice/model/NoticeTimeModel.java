package com.xiaotu.makeplays.notice.model;

import java.util.Date;

/**
 * 通告单生成信息
 * @author xuchangjian
 */
public class NoticeTimeModel {

	public static final String TABLE_NAME="tab_notice_time";
	
	private String noticeTimeId;	//ID
	private String breakfastTime;	//早餐时间
	private String departureTime;	//出发时间
	private String note;	//特别提示
	private String crewId;	//剧本ID
	private String noticeId;	//通告单ID
	private Date createTime;	//创建时间
	private String remark; 	//备注
	private String version; 	//版本信息
	private String groupDirector;	//组导演
	private String shootGuide;	//摄影指导
	private String insideAdvert;	//商植
	private String roleConvertRemark;	//演员转场提示
	private String roleInfo;	//演员信息
	private String shootLocationInfos;	//拍摄地点信息
	private String weatherInfo;	//天气信息
	private Date updateTime;	//最后修改时间
	private String noticeContact; //通告单剧组联系表
	
	
	public String getNoticeContact() {
		return noticeContact;
	}
	public void setNoticeContact(String noticeContact) {
		this.noticeContact = noticeContact;
	}
	public Date getUpdateTime() {
		return this.updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getWeatherInfo() {
		return this.weatherInfo;
	}
	public void setWeatherInfo(String weatherInfo) {
		this.weatherInfo = weatherInfo;
	}
	public String getShootLocationInfos() {
		return this.shootLocationInfos;
	}
	public void setShootLocationInfos(String shootLocationInfos) {
		this.shootLocationInfos = shootLocationInfos;
	}
	public String getRoleInfo() {
		return this.roleInfo;
	}
	public void setRoleInfo(String roleInfo) {
		this.roleInfo = roleInfo;
	}
	public String getNoticeTimeId() {
		return this.noticeTimeId;
	}
	public void setNoticeTimeId(String noticeTimeId) {
		this.noticeTimeId = noticeTimeId;
	}
	public String getBreakfastTime() {
		return this.breakfastTime;
	}
	public void setBreakfastTime(String breakfastTime) {
		this.breakfastTime = breakfastTime;
	}
	public String getDepartureTime() {
		return this.departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
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
	public Date getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getVersion() {
		return this.version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getGroupDirector() {
		return this.groupDirector;
	}
	public void setGroupDirector(String groupDirector) {
		this.groupDirector = groupDirector;
	}
	public String getShootGuide() {
		return this.shootGuide;
	}
	public void setShootGuide(String shootGuide) {
		this.shootGuide = shootGuide;
	}
	public String getInsideAdvert() {
		return this.insideAdvert;
	}
	public void setInsideAdvert(String insideAdvert) {
		this.insideAdvert = insideAdvert;
	}
	public String getRoleConvertRemark() {
		return this.roleConvertRemark;
	}
	public void setRoleConvertRemark(String roleConvertRemark) {
		this.roleConvertRemark = roleConvertRemark;
	}
}
