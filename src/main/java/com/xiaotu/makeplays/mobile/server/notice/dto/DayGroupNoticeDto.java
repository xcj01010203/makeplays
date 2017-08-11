package com.xiaotu.makeplays.mobile.server.notice.dto;

import java.util.List;

public class DayGroupNoticeDto {

	/**
	 * 日期，格式：yyyy-MM-dd
	 */
	private String day;
	
	/**
	 * 通告单列表
	 */
	private List<NoticeInfoDto> noticeList;

	public String getDay() {
		return this.day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public List<NoticeInfoDto> getNoticeList() {
		return this.noticeList;
	}

	public void setNoticeList(List<NoticeInfoDto> noticeList) {
		this.noticeList = noticeList;
	}
	
}
