package com.xiaotu.makeplays.community.model;

import java.util.Date;

/**
 * 举报信息表
 * @author wanrenyi 2016年9月3日下午4:43:59
 */
public class ReportInfoModel {

	public static final String TABLE_NAME = "tab_report_info";
	
	/**
	 *举报id 
	 */
	private String reportId;
	
	/**
	 * 组训id
	 */
	private String teamId;
	
	/**
	 * 举报人id
	 */
	private String userId;
	
	/**
	 * 举报类型 1虚假广告，2色情低俗，3违法违纪，4 咋骗信息; 详情参见 reportType枚举类
	 */
	private Integer reportType;
	
	/**
	 * 举报说明
	 */
	private String reportComment;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getReportType() {
		return reportType;
	}
	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}
	public String getReportComment() {
		return reportComment;
	}
	public void setReportComment(String reportComment) {
		this.reportComment = reportComment;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
