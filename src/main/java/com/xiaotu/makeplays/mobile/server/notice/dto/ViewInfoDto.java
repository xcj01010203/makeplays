package com.xiaotu.makeplays.mobile.server.notice.dto;


/**
 * 场景信息
 * @author xuchangjian
 */
public class ViewInfoDto {

	/**
	 * 集
	 */
	private int seriesNo;
	
	/**
	 * 场
	 */
	private String viewNo;
	
	/**
	 * 主场景信息，多个用逗号隔开
	 */
	private String viewLocation;
	
	/**
	 * 页量
	 */
	private double pageCount;
	
	/**
	 * 气氛
	 */
	private String atmosphereName;
	
	/**
	 * 内外景
	 */
	private String site;
	
	/**
	 * 文武戏
	 */
	private Integer viewType;
	
	/**
	 * 主要内容
	 */
	private String mainContent;
	
	/**
	 * 主要演员名称
	 */
	private String mainRoleNames;
	
	/**
	 * 主要演员简称
	 */
	private String mainRoleShortNames;
	
	/**
	 * 特约演员名称（多个用逗号隔开）
	 */
	private String guestRoleNames;
	
	/**
	 * 群众演员名称（多个用逗号隔开）
	 */
	private String massRoleNames;
	
	/**
	 * 服装（多个用逗号隔开）
	 */
	private String clothesNames;
	
	/**
	 * 化妆（多个用逗号隔开）
	 */
	private String makeupNames;
	
	/**
	 * 道具（多个用逗号隔开）
	 */
	private String propNames;
	
	/**
	 * 特殊道具（多个用逗号隔开）
	 */
	private String specialPropName;
	
	/**
	 * 商植
	 */
	private String insertAdverts;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 是否是备戏
	 */
	private Boolean prepareStatus;

	public Boolean getPrepareStatus() {
		return this.prepareStatus;
	}

	public void setPrepareStatus(Boolean prepareStatus) {
		this.prepareStatus = prepareStatus;
	}

	public String getInsertAdverts() {
		return this.insertAdverts;
	}

	public void setInsertAdverts(String insertAdverts) {
		this.insertAdverts = insertAdverts;
	}

	public Integer getViewType() {
		return this.viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}

	public String getSpecialPropName() {
		return this.specialPropName;
	}

	public void setSpecialPropName(String specialPropName) {
		this.specialPropName = specialPropName;
	}

	public int getSeriesNo() {
		return this.seriesNo;
	}

	public void setSeriesNo(int seriesNo) {
		this.seriesNo = seriesNo;
	}

	public String getViewNo() {
		return this.viewNo;
	}

	public void setViewNo(String viewNo) {
		this.viewNo = viewNo;
	}

	public String getViewLocation() {
		return this.viewLocation;
	}

	public void setViewLocation(String viewLocation) {
		this.viewLocation = viewLocation;
	}

	public double getPageCount() {
		return this.pageCount;
	}

	public void setPageCount(double pageCount) {
		this.pageCount = pageCount;
	}

	public String getAtmosphereName() {
		return this.atmosphereName;
	}

	public void setAtmosphereName(String atmosphereName) {
		this.atmosphereName = atmosphereName;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getMainContent() {
		return this.mainContent;
	}

	public void setMainContent(String mainContent) {
		this.mainContent = mainContent;
	}

	public String getMainRoleNames() {
		return this.mainRoleNames;
	}

	public void setMainRoleNames(String mainRoleNames) {
		this.mainRoleNames = mainRoleNames;
	}

	public String getMainRoleShortNames() {
		return this.mainRoleShortNames;
	}

	public void setMainRoleShortNames(String mainRoleShortNames) {
		this.mainRoleShortNames = mainRoleShortNames;
	}

	public String getGuestRoleNames() {
		return this.guestRoleNames;
	}

	public void setGuestRoleNames(String guestRoleNames) {
		this.guestRoleNames = guestRoleNames;
	}

	public String getMassRoleNames() {
		return this.massRoleNames;
	}

	public void setMassRoleNames(String massRoleNames) {
		this.massRoleNames = massRoleNames;
	}

	public String getClothesNames() {
		return this.clothesNames;
	}

	public void setClothesNames(String clothesNames) {
		this.clothesNames = clothesNames;
	}

	public String getMakeupNames() {
		return this.makeupNames;
	}

	public void setMakeupNames(String makeupNames) {
		this.makeupNames = makeupNames;
	}

	public String getPropNames() {
		return this.propNames;
	}

	public void setPropNames(String propNames) {
		this.propNames = propNames;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
