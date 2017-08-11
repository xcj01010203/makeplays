package com.xiaotu.makeplays.view.model;

import java.util.Date;

/**
 * 场景信息表
 * @author xuchangjian
 */
public class ViewInfoModel {

	public static final String TABLE_NAME = "tab_view_info";
	

	/**
	 * 拍摄时间
	 */
	private Date shotDate;
	
	
	/**
	 * 场景ID
	 */
	private String viewId;
	
	/**
	 * 集次
	 */
	private Integer seriesNo;
	
	/**
	 * 场次
	 */
	private String viewNo;
	
	/**
	 * 季节。值见Season枚举类
	 */
	private Integer season;
	
	/**
	 * 气氛ID
	 */
	private String atmosphereId;
	
	/**
	 * 内外景
	 */
	private String site;
	
	/**
	 * 页数
	 */
	private Double pageCount;
	
	/**
	 * 场景类型。值见ViewType枚举类
	 */
	private Integer viewType;
	
	/**
	 * 主要内容
	 */
	private String mainContent;
	
	/**
	 * 拍摄地点ID
	 */
	private String shootLocationId;
	
	/**
	 * 拍摄状态。值见ShootStatus枚举类
	 */
	private int shootStatus = 0;
	
	/**
	 * 拍摄状态备注
	 */
	private String statusRemark;
	
	/**
	 * 拍摄状态最后更新时间
	 */
	private Date statusUpdateTime;
	
	/**
	 * 设置场景为完成/加戏/删戏状态的通告单ID
	 */
	private String noticeId;
	
	/**
	 * 是否手工保存。
	 */
	private boolean isManualSave = false;
	
	/**
	 * 创建方式。值见ViewCreateWay枚举类
	 */
	private int createWay;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 剧本ID
	 */
	private String crewId;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 未提取的角色，多个用/隔开
	 */
	private String notGetRoleNames;
	
	/**
	 * 未提取的角色，多个用/隔开
	 */
	private String notGetProps;
	
	/**
	 * 特殊提醒
	 */
	private String specialRemind;
	
	public String getSpecialRemind() {
		return specialRemind;
	}

	public void setSpecialRemind(String specialRemind) {
		this.specialRemind = specialRemind;
	}

	public String getNotGetProps() {
		return this.notGetProps;
	}

	public void setNotGetProps(String notGetProps) {
		this.notGetProps = notGetProps;
	}

	public String getNotGetRoleNames() {
		return this.notGetRoleNames;
	}

	public void setNotGetRoleNames(String notGetRoleNames) {
		this.notGetRoleNames = notGetRoleNames;
	}

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
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

	public Integer getSeason() {
		return this.season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}

	public String getAtmosphereId() {
		return this.atmosphereId;
	}

	public void setAtmosphereId(String atmosphereId) {
		this.atmosphereId = atmosphereId;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Double getPageCount() {
		return this.pageCount;
	}

	public void setPageCount(Double pageCount) {
		this.pageCount = pageCount;
	}

	public Integer getViewType() {
		return this.viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}

	public String getMainContent() {
		return this.mainContent;
	}

	public void setMainContent(String mainContent) {
		this.mainContent = mainContent;
	}

	public String getShootLocationId() {
		return this.shootLocationId;
	}

	public void setShootLocationId(String shootLocationId) {
		this.shootLocationId = shootLocationId;
	}

	public int getShootStatus() {
		return this.shootStatus;
	}

	public void setShootStatus(int shootStatus) {
		this.shootStatus = shootStatus;
	}

	public String getStatusRemark() {
		return this.statusRemark;
	}

	public void setStatusRemark(String statusRemark) {
		this.statusRemark = statusRemark;
	}

	public Date getStatusUpdateTime() {
		return this.statusUpdateTime;
	}

	public void setStatusUpdateTime(Date statusUpdateTime) {
		this.statusUpdateTime = statusUpdateTime;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public boolean getIsManualSave() {
		return this.isManualSave;
	}

	public void setIsManualSave(boolean isManualSave) {
		this.isManualSave = isManualSave;
	}

	public int getCreateWay() {
		return this.createWay;
	}

	public void setCreateWay(int createWay) {
		this.createWay = createWay;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getShotDate() {
		return shotDate;
	}

	public void setShotDate(Date shotDate) {
		this.shotDate = shotDate;
	}

}
