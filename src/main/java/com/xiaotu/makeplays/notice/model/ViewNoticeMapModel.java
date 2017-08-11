package com.xiaotu.makeplays.notice.model;

/**
 * 场景与通告单关联信息表
 * @author wanrenyi 2016年8月4日下午6:05:08
 */
public class ViewNoticeMapModel {

	public static final String TABLE_NAME="tab_view_notice_map";
	
	/**
	 * 场景与通告单关联信息ID
	 */
	private String mapId;
	
	/**
	 * 场景id
	 */
	private String viewId;
	
	/**
	 * 通告单id
	 */
	private String noticeId;
	
	/**
	 * 排列顺序
	 */
	private Integer sequence;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 拍摄状态 0:甩戏；1:部分完成；2:完成；3:删戏；4:加戏部分完成；5:加戏已完成；'
	 */
	private Integer shootStatus;
	
	/**
	 * 拍摄状态备注
	 */
	private String statusRemark;
	
	/**
	 * 磁带号
	 */
	private String tapNo;
	
	/**
	 * 是否是预备场景
	 */
	private Integer prepareView;
	
	/**
	 * 实际拍摄页数
	 */
	private Double shootPage;
	
	public Integer getPrepareView() {
		return prepareView;
	}
	public void setPrepareView(Integer prepareView) {
		this.prepareView = prepareView;
	}
	public Double getShootPage() {
		return shootPage;
	}
	public void setShootPage(Double shootPage) {
		this.shootPage = shootPage;
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getViewId() {
		return viewId;
	}
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public Integer getShootStatus() {
		return shootStatus;
	}
	public void setShootStatus(Integer shootStatus) {
		this.shootStatus = shootStatus;
	}
	public String getStatusRemark() {
		return statusRemark;
	}
	public void setStatusRemark(String statusRemark) {
		this.statusRemark = statusRemark;
	}
	public String getTapNo() {
		return tapNo;
	}
	public void setTapNo(String tapNo) {
		this.tapNo = tapNo;
	}
	
	
}
