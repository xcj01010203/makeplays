package com.xiaotu.makeplays.mobile.server.notice.dto;

import java.util.List;
import java.util.Map;

/**
 * 通告单生成信息
 * @author xuchangjian
 */
public class NoticeTimeDto {

	/**
	 * 通告单名称
	 */
	private String noticeName;
	
	/**
	 * 通告单版本
	 */
	private String version;
	
	/**
	 * 组导演
	 */
	private String groupDirector;
	
	/**
	 * 周几
	 */
	private String weekday;
	
	/**
	 * 拍摄第几天
	 */
	private int shootDays;
	
	/**
	 * 总场数
	 */
	private int totalViewnum;
	
	/**
	 * 总页数
	 */
	private double totalPagenum;
	
	/**
	 * 气氛内外景统计信息
	 */
	private String statistics;
	
	/**
	 * 早餐时间
	 */
	private String breakfastTime;
	
	/**
	 * 出发时间
	 */
	private String departureTime;
	
	/**
	 * 拍摄地点
	 */
	private String shootLocationInfos;
	
	/**
	 * 天气情况
	 */
	private String weatherInfo;
	
	/**
	 * 角色调度
	 */
	private String roleInfo;
	
	/**
	 * 商植
	 */
	private String insideAdvert;
	
	/**
	 * 特别提示
	 */
	private String note;
	
	/**
	 * 警示信息
	 */
	private String remark;
	
	/**
	 * 其他提示
	 */
	private String otherTips;
	
	/**
	 * 图片信息
	 */
	private List<PictureDto> pictureInfo;
	
	/**
	 * 发布的通告单最后修改时间
	 */
	private String noticeTimeUpdateTime;
	
	/**
	 * 联系人列表
	 */
	private String noticeContact;
	
	/**
	 * 场景拍摄地点的经纬度信息列表
	 */
	private List<Map<String, Object>> viewLocationInfoList;

	public List<Map<String, Object>> getViewLocationInfoList() {
		return viewLocationInfoList;
	}

	public void setViewLocationInfoList(
			List<Map<String, Object>> viewLocationInfoList) {
		this.viewLocationInfoList = viewLocationInfoList;
	}

	public String getNoticeContact() {
		return this.noticeContact;
	}

	public void setNoticeContact(String noticeContact) {
		this.noticeContact = noticeContact;
	}

	public String getInsideAdvert() {
		return this.insideAdvert;
	}

	public void setInsideAdvert(String insideAdvert) {
		this.insideAdvert = insideAdvert;
	}

	public String getGroupDirector() {
		return this.groupDirector;
	}

	public void setGroupDirector(String groupDirector) {
		this.groupDirector = groupDirector;
	}

	public String getOtherTips() {
		return this.otherTips;
	}

	public void setOtherTips(String otherTips) {
		this.otherTips = otherTips;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<PictureDto> getPictureInfo() {
		return this.pictureInfo;
	}

	public void setPictureInfo(List<PictureDto> pictureInfo) {
		this.pictureInfo = pictureInfo;
	}

	public String getNoticeName() {
		return this.noticeName;
	}

	public void setNoticeName(String noticeName) {
		this.noticeName = noticeName;
	}

	public String getNoticeTimeUpdateTime() {
		return this.noticeTimeUpdateTime;
	}

	public void setNoticeTimeUpdateTime(String noticeTimeUpdateTime) {
		this.noticeTimeUpdateTime = noticeTimeUpdateTime;
	}

	public String getStatistics() {
		return this.statistics;
	}

	public void setStatistics(String statistics) {
		this.statistics = statistics;
	}

	public String getWeekday() {
		return this.weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public int getShootDays() {
		return this.shootDays;
	}

	public void setShootDays(int shootDays) {
		this.shootDays = shootDays;
	}

	public int getTotalViewnum() {
		return this.totalViewnum;
	}

	public void setTotalViewnum(int totalViewnum) {
		this.totalViewnum = totalViewnum;
	}

	public double getTotalPagenum() {
		return this.totalPagenum;
	}

	public void setTotalPagenum(double totalPagenum) {
		this.totalPagenum = totalPagenum;
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

	public String getShootLocationInfos() {
		return this.shootLocationInfos;
	}

	public void setShootLocationInfos(String shootLocationInfos) {
		this.shootLocationInfos = shootLocationInfos;
	}

	public String getWeatherInfo() {
		return this.weatherInfo;
	}

	public void setWeatherInfo(String weatherInfo) {
		this.weatherInfo = weatherInfo;
	}

	public String getRoleInfo() {
		return this.roleInfo;
	}

	public void setRoleInfo(String roleInfo) {
		this.roleInfo = roleInfo;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
