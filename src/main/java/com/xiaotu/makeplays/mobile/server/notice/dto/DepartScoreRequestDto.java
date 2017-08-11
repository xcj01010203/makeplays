package com.xiaotu.makeplays.mobile.server.notice.dto;

import java.util.List;

/**
 * 部门评分请求Dto
 * @author xuchangjian 2016-3-3上午11:29:52
 */
public class DepartScoreRequestDto {

	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 部门评分列表 
	 */
	private List<DepartmentEvaluateDto> departScoreList;

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public List<DepartmentEvaluateDto> getDepartScoreList() {
		return this.departScoreList;
	}

	public void setDepartScoreList(List<DepartmentEvaluateDto> departScoreList) {
		this.departScoreList = departScoreList;
	}
}
