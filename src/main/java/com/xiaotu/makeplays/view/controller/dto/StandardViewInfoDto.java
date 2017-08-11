package com.xiaotu.makeplays.view.controller.dto;

/**
 * 名称标准化的场景信息DTO，新的前台框架全部采用该标准格式
 * @author xuchangjian 2015-12-31下午3:22:01
 */
public class StandardViewInfoDto {

	private String crewId;	//剧组ID
	private String viewId;	//场景ID
	private Integer seriesNo;	//集次
	private String viewNo;	//场次
	private Double pageCount;	//页数
	private String mainContent;	//主要内容
	private String remark;	//备注

	private String shootLocation;	//拍摄地
	private String shootRegion;	//地域
	private String lvlOneLocation;	//主场景
	private String lvlTwoLocation;	//次场景
	private String lvlThreeLocation;	//三级场景

	private String leadingRoles;	//主演
	private String guestRoles;	//特约演员
	private String massesRoles;	//群众演员

	private String atmosphereName;	//气氛
	private String site;	//内外景

	private String clothes;	//服装
	private String makeups;	//化妆
	private String commonProps;	//普通道具
	private String specialProps;	//特殊道具
	private String adverts;	//广告
    
	private Integer shootStatus;	//拍摄状态枚举值
	private String shootStatusName;	//拍摄状态名称
	private String title;	//剧本标题
	private String content;	//剧本内容
	private Boolean isManualSave;	//是否手动保存
	
	public String getAdverts() {
		return this.adverts;
	}
	public void setAdverts(String adverts) {
		this.adverts = adverts;
	}
	public String getCrewId() {
		return this.crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public String getViewId() {
		return this.viewId;
	}
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	public String getViewNo() {
		return this.viewNo;
	}
	public void setViewNo(String viewNo) {
		this.viewNo = viewNo;
	}
	public Double getPageCount() {
		return this.pageCount;
	}
	public void setPageCount(Double pageCount) {
		this.pageCount = pageCount;
	}
	public String getMainContent() {
		return this.mainContent;
	}
	public void setMainContent(String mainContent) {
		this.mainContent = mainContent;
	}
	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getShootLocation() {
		return this.shootLocation;
	}
	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}
	public String getLvlOneLocation() {
		return this.lvlOneLocation;
	}
	public void setLvlOneLocation(String lvlOneLocation) {
		this.lvlOneLocation = lvlOneLocation;
	}
	public String getLvlTwoLocation() {
		return this.lvlTwoLocation;
	}
	public void setLvlTwoLocation(String lvlTwoLocation) {
		this.lvlTwoLocation = lvlTwoLocation;
	}
	public String getLvlThreeLocation() {
		return this.lvlThreeLocation;
	}
	public void setLvlThreeLocation(String lvlThreeLocation) {
		this.lvlThreeLocation = lvlThreeLocation;
	}
	public String getLeadingRoles() {
		return this.leadingRoles;
	}
	public void setLeadingRoles(String leadingRoles) {
		this.leadingRoles = leadingRoles;
	}
	public String getGuestRoles() {
		return this.guestRoles;
	}
	public void setGuestRoles(String guestRoles) {
		this.guestRoles = guestRoles;
	}
	public String getMassesRoles() {
		return this.massesRoles;
	}
	public void setMassesRoles(String massesRoles) {
		this.massesRoles = massesRoles;
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
	public String getClothes() {
		return this.clothes;
	}
	public void setClothes(String clothes) {
		this.clothes = clothes;
	}
	public String getMakeups() {
		return this.makeups;
	}
	public void setMakeups(String makeups) {
		this.makeups = makeups;
	}
	public String getCommonProps() {
		return this.commonProps;
	}
	public void setCommonProps(String commonProps) {
		this.commonProps = commonProps;
	}
	public String getSpecialProps() {
		return this.specialProps;
	}
	public void setSpecialProps(String specialProps) {
		this.specialProps = specialProps;
	}
	public Integer getSeriesNo() {
		return this.seriesNo;
	}
	public void setSeriesNo(Integer seriesNo) {
		this.seriesNo = seriesNo;
	}
	public Integer getShootStatus() {
		return this.shootStatus;
	}
	public void setShootStatus(Integer shootStatus) {
		this.shootStatus = shootStatus;
	}
	public String getShootStatusName() {
		return this.shootStatusName;
	}
	public void setShootStatusName(String shootStatusName) {
		this.shootStatusName = shootStatusName;
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Boolean getIsManualSave() {
		return this.isManualSave;
	}
	public void setIsManualSave(Boolean isManualSave) {
		this.isManualSave = isManualSave;
	}
	public String getShootRegion() {
		return shootRegion;
	}
	public void setShootRegion(String shootRegion) {
		this.shootRegion = shootRegion;
	}
}
