package com.xiaotu.makeplays.mobile.server.notice.dto;

import java.util.List;

/**
 * 一个月的通告单
 * @author xuchangjian 2016-9-18下午6:38:56
 */
public class MonthGroupNoticeDto {

	/**
	 * 月份，格式：yyyy-MM
	 */
	private String month;
	
	/**
	 * 当月所有按天分组的通告单列表
	 */
	private List<DayGroupNoticeDto> dayGroupNoticeList;

	public String getMonth() {
		return this.month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public List<DayGroupNoticeDto> getDayGroupNoticeList() {
		return this.dayGroupNoticeList;
	}

	public void setDayGroupNoticeList(List<DayGroupNoticeDto> dayGroupNoticeList) {
		this.dayGroupNoticeList = dayGroupNoticeList;
	}
}
