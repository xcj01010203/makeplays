package com.xiaotu.makeplays.view.model;

/**
 * 场景临时信息表
 * @author xuchangjian
 */
public class ViewTempModel {

	public static final String 	TABLE_NAME = "tab_view_temp";
	/*
	 * 商植
	 * 
	 */
	private String commercialImplants;
	//拍摄时间
	private String shootTime;
	
	/**
	 * 临时表ID
	 */
	private String viewTempId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 集次
	 */
	private Integer seriesNo;
	
	/**
	 * 场次
	 */
	private String viewNo;
	
	/**
	 * 气氛
	 */
	private String atmosphere;
	
	/**
	 * 内外景
	 */
	private String site;
	
	/**
	 * 每一场的标题
	 */
	private String title;
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 角色列表
	 */
	private String roleNames;
	
	/**
	 * 特约演员
	 */
	private String guestNames;
	
	/**
	 * 群众演员
	 */
	private String massNames;
	
	/**
	 * 主场景
	 */
	private String firstLocation;
	
	/**
	 * 次场景
	 */
	private String secondLocation;
	
	/**
	 * 三级场景
	 */
	private String thirdLocation;
	
	/**
	 * 数据类型（1:供用户选择“跳过”或“替换”的数据  2:供用户选择“保留”或“不保留”的数据，详细信息见SceneTempDataType枚举类）
	 */
	private Integer dataType;
	
	/**
	 * 页数
	 */
	private Double pageCount;
	
	/**
	 * 剧本类型（1：剧本标头在一行；2：剧本标头分行，详细信息见PlayType枚举类）
	 */
	private int type = 1;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 道具
	 */
	private String propsNames;
	
	/**
	 * 特殊道具
	 */
	private String specialProps;
	
	/**
	 * 季节
	 */
	private Integer season;
	
	/**
	 * 拍摄地点
	 */
	private String shootLocation;
	
	/**
	 * 服装
	 */
	private String clothesNames;
	
	/**
	 * 化妆
	 */
	private String makeupNames;
	
	/**
	 * 文武戏
	 */
	private Integer viewType;
	
	/**
	 * 主要内容
	 */
	private String mainContent;

	public String getMainContent() {
		return this.mainContent;
	}

	public void setMainContent(String mainContent) {
		this.mainContent = mainContent;
	}

	public Integer getSeason() {
		return this.season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}

	public String getShootLocation() {
		return this.shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
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

	public Integer getViewType() {
		return this.viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getViewTempId() {
		return this.viewTempId;
	}

	public void setViewTempId(String sceneTempId) {
		this.viewTempId = sceneTempId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public Integer getSeriesNo() {
		return this.seriesNo;
	}

	public void setSeriesNo(Integer setNo) {
		this.seriesNo = setNo;
	}

	public String getViewNo() {
		return this.viewNo;
	}

	public void setViewNo(String viewNo) {
		this.viewNo = viewNo;
	}

	public String getAtmosphere() {
		return this.atmosphere;
	}

	public void setAtmosphere(String atmosphere) {
		this.atmosphere = atmosphere;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
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

	public String getRoleNames() {
		return this.roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public String getFirstLocation() {
		return this.firstLocation;
	}

	public void setFirstLocation(String firstLocation) {
		this.firstLocation = firstLocation;
	}

	public String getSecondLocation() {
		return this.secondLocation;
	}

	public void setSecondLocation(String secondLocation) {
		this.secondLocation = secondLocation;
	}

	public String getThirdLocation() {
		return this.thirdLocation;
	}

	public void setThirdLocation(String thirdLocation) {
		this.thirdLocation = thirdLocation;
	}

	public Integer getDataType() {
		return this.dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Double getPageCount() {
		return this.pageCount;
	}

	public void setPageCount(Double pageCount) {
		this.pageCount = pageCount;
	}

	public String getGuestNames() {
		return this.guestNames;
	}

	public void setGuestNames(String guestNames) {
		this.guestNames = guestNames;
	}

	public String getMassNames() {
		return this.massNames;
	}

	public void setMassNames(String massNames) {
		this.massNames = massNames;
	}

	public String getPropsNames() {
		return this.propsNames;
	}

	public void setPropsNames(String propsNames) {
		this.propsNames = propsNames;
	}

	public String getSpecialProps() {
		return specialProps;
	}

	public void setSpecialProps(String specialProps) {
		this.specialProps = specialProps;
	}

	public String getShootTime() {
		return shootTime;
	}

	public void setShootTime(String shootTime) {
		this.shootTime = shootTime;
	}

	public String getCommercialImplants() {
		return commercialImplants;
	}

	public void setCommercialImplants(String commercialImplants) {
		this.commercialImplants = commercialImplants;
	}
	
}
