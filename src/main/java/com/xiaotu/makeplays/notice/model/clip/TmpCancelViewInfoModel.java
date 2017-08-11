package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 场记单临时销场申请信息
 * @author xuchangjian 2015-11-12上午9:58:59
 */
public class TmpCancelViewInfoModel {

	public static final String TABLE_NAME = "tab_tmpCancelView_info";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 操作人ID
	 */
	private String userId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 集次
	 */
	private Integer seriesNo;
	
	/**
	 * 场次
	 */
	private String viewNo;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 拍摄状态，0:甩戏；1:部分完成；2:完成；3:删戏；4:加戏部分完成；5:加戏已完成；
	 */
	private Integer shootStatus;
	
	/**
	 * 完成日期
	 */
	private Date finishDate;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 带号
	 */
	private String tapNo;
	
	/**
	 * 是否已做确认销场处理
	 */
	private boolean hasDealed;

	public Date getFinishDate() {
		return this.finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
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

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public Integer getSeriesNo() {
		return this.seriesNo;
	}

	public void setSeriesNo(Integer seriesNo) {
		this.seriesNo = seriesNo;
	}

	public String getViewNo() {
		return this.viewNo;
	}

	public void setViewNo(String viewNo) {
		this.viewNo = viewNo;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getShootStatus() {
		return this.shootStatus;
	}

	public void setShootStatus(Integer shootStatus) {
		this.shootStatus = shootStatus;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTapNo() {
		return this.tapNo;
	}

	public void setTapNo(String tapNo) {
		this.tapNo = tapNo;
	}

	public boolean getHasDealed() {
		return this.hasDealed;
	}

	public void setHasDealed(boolean hasDealed) {
		this.hasDealed = hasDealed;
	}
}
