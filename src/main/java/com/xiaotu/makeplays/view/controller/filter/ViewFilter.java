package com.xiaotu.makeplays.view.controller.filter;

/**
 * 用于高级查询的场景过滤对象
 * @author cuchangjian 2016年8月4日上午10:10:34
 */
public class ViewFilter {

	/**
	 * 集次编号
	 */
	private Integer seriesNo;
	
	/**
	 * 场次编号
	 */
	private String viewNo;
	
	/**
	 * 查询起始集次编号
	 */
	private Integer startSeriesNo;
	
	/**
	 * 查询完结集次编号
	 */
	private Integer endSeriesNo;
	
	/**
	 * 查询起始场次编号
	 */
	private String startViewNo;
	
	/**
	 * 查询完结场次编号
	 */
	private String endViewNo;
	
	/**
	 * 集场编号，多个集场编号以锋号或逗号隔开，例：1-1；1-2,1-3
	 */
	private String seriesViewNos;
	
	/**
	 * 季节
	 */
	private String season;
	
	/**
	 * 内外景
	 */
	private String  site; 
	
	/**
	 * 气氛
	 */
	private String atmosphere;
	
	/**
	 * 拍摄状态
	 */
	private String shootStatus;
	
	/**
	 * 排序方式:1代表默认升序排序;其它代表降序排序
	 */
	private Integer sortFlag= new Integer(1);
	
	/**
	 * 顺场/分场: 1代表按照场次编号排序(顺场); 2代表按照地址排序(分场)
	 */
	private Integer sortType= new Integer(1);
	
	/**
	 * 文武戏
	 */
	private String viewType ;
	
	/**
	 * 角色id字符串,多个角色id之间以","分隔
	 */
	private String roles;

	/**
	 * 道具
	 */
	private String props;
	
	/**
	 * 特殊道具
	 */
	private String specialProps;
	
	/**
	 * 群众演员
	 */
	private String mass;
	
	/**
	 * 特约演员
	 */
	private String guest;
	
	/**
	 * 拍摄地点
	 */
	private String shootLocation;
	
	/**
	 * 拍摄地点（模糊查询）
	 */
	private String shootLocationLike;
	
	/**
	 * 主场景
	 */
	private String major;
	
	/**
	 * 主场景（模糊查询）
	 */
	private String majorLike;
	
	/**
	 * 次场景
	 */
	private String minor;
	
	/**
	 * 次场景（模糊查询）
	 */
	private String minorLike;
	
	/**
	 * 三级场景
	 */
	private String thirdLevel; 
	/**
	 * 三级场景（模糊查询）
	 */
	private String thirdLevelLike;
	/**
	 * 化妆
	 */
	private String makeup;
	
	/**
	 * 服装
	 */
	private String clothes;
	
	/**
	 * 广告
	 */
	private String advert;
	
	/**
	 * 角色与演员的同时存在关系: 0 代表同时存在; 1 代表存在即可; 2代表选择不同时出现的场次(正确逻辑为在存在即可的基础上过滤掉同时出现的场景); 3代表不同时存在
	 */
	private String searchMode;
	
	/**
	 * 是否查询全部数据
	 */
	private String isAll;
	
	/**
	 * 标识查询是来自高级查询功能，默认为false
	 */
	//private boolean fromAdvance = false;
	
	/**
	 * 通告单
	 */
	private String noticeId;
	
	/**
	 * 拍摄计划ID，用于查询和拍摄计划相关的场景信息
	 */
	private String planId;
	
	/**
	 * 主要内容
	 */
	private String mainContent;
		
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 场景表id,多个id以逗号分隔
	 */
	private String viewIds;
	
	/**
	 * 是否导出剧本
	 */
	private Boolean exportViewContent;
	
	/**
	 * 是否导出场景内容
	 */
	private Boolean exportViewInfo;
	
	/**
	 * 最小场数
	 */
	private Integer minViewNum;
	
	/**
	 * 最大场数
	 */
	private Integer maxViewNum;
	
	/**
	 * 完成度
	 */
	private String completion;
	
	/**
	 * 特殊提醒
	 */
	private String specialRemind;
	
	/**
	 * 计划分组ID
	 */
	private String scheduleGroupId;
	
	/**
	 * 排序字段
	 */
	private String sortField;
	
	/**
	 * 计划日期+计划分组ID
	 */
	private String scheduleIds;
	
	public String getSpecialRemind() {
		return specialRemind;
	}

	public void setSpecialRemind(String specialRemind) {
		this.specialRemind = specialRemind;
	}

	public Boolean getExportViewInfo() {
		return exportViewInfo;
	}

	public void setExportViewInfo(Boolean exportViewInfo) {
		this.exportViewInfo = exportViewInfo;
	}

	public Boolean getExportViewContent() {
		return exportViewContent;
	}

	public void setExportViewContent(Boolean exportViewContent) {
		this.exportViewContent = exportViewContent;
	}

	public String getViewIds() {
		return this.viewIds;
	}

	public void setViewIds(String viewIds) {
		this.viewIds = viewIds;
	}

	public String getSpecialProps() {
		return this.specialProps;
	}

	public void setSpecialProps(String specialProps) {
		this.specialProps = specialProps;
	}

	public String getSeriesViewNos() {
		return this.seriesViewNos;
	}

	public void setSeriesViewNos(String seriesViewNos) {
		this.seriesViewNos = seriesViewNos;
	}

	public String getPlanId() {
		return this.planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
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

	public Integer getSeriesNo() {
		return seriesNo;
	}

	public void setSeriesNo(Integer seriesNo) {
		this.seriesNo = seriesNo;
	}

	public String getViewNo() {
		return viewNo;
	}

	public void setViewNo(String viewNo) {
		this.viewNo = viewNo;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getAtmosphere() {
		return atmosphere;
	}

	public void setAtmosphere(String atmosphere) {
		this.atmosphere = atmosphere;
	}

	public String getShootStatus() {
		return shootStatus;
	}

	public void setShootStatus(String shootStatus) {
		this.shootStatus = shootStatus;
	}

	public Integer getSortFlag() {
		return sortFlag;
	}

	public void setSortFlag(Integer sortFlag) {
		this.sortFlag = sortFlag;
	}

	public Integer getSortType() {
		return sortType;
	}

	public void setSortType(Integer sortType) {
		this.sortType = sortType;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getProps() {
		return props;
	}

	public void setProps(String props) {
		this.props = props;
	}

	public String getMass() {
		return mass;
	}

	public void setMass(String mass) {
		this.mass = mass;
	}

	public String getGuest() {
		return guest;
	}

	public void setGuest(String guest) {
		this.guest = guest;
	}

	public Integer getStartSeriesNo() {
		return startSeriesNo;
	}

	public void setStartSeriesNo(Integer startSeriesNo) {
		this.startSeriesNo = startSeriesNo;
	}

	public Integer getEndSeriesNo() {
		return this.endSeriesNo;
	}

	public void setEndSeriesNo(Integer endSeriesNo) {
		this.endSeriesNo = endSeriesNo;
	}

	public String getStartViewNo() {
		return this.startViewNo;
	}

	public void setStartViewNo(String startViewNo) {
		this.startViewNo = startViewNo;
	}

	public String getEndViewNo() {
		return this.endViewNo;
	}

	public void setEndViewNo(String endViewNo) {
		this.endViewNo = endViewNo;
	}

	public String getShootLocation() {
		return shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getThirdLevel() {
		return thirdLevel;
	}

	public void setThirdLevel(String thirdLevel) {
		this.thirdLevel = thirdLevel;
	}

	public String getMakeup() {
		return makeup;
	}

	public void setMakeup(String makeup) {
		this.makeup = makeup;
	}

	public String getClothes() {
		return clothes;
	}

	public void setClothes(String clothes) {
		this.clothes = clothes;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

	public String getAdvert() {
		return advert;
	}

	public void setAdvert(String advert) {
		this.advert = advert;
	}

	public String getIsAll() {
		return isAll;
	}

	public void setIsAll(String isAll) {
		this.isAll = isAll;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public Integer getMinViewNum() {
		return minViewNum;
	}

	public void setMinViewNum(Integer minViewNum) {
		this.minViewNum = minViewNum;
	}

	public Integer getMaxViewNum() {
		return maxViewNum;
	}

	public void setMaxViewNum(Integer maxViewNum) {
		this.maxViewNum = maxViewNum;
	}

	public String getCompletion() {
		return completion;
	}

	public void setCompletion(String completion) {
		this.completion = completion;
	}

	public String getScheduleGroupId() {
		return scheduleGroupId;
	}

	public void setScheduleGroupId(String scheduleGroupId) {
		this.scheduleGroupId = scheduleGroupId;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getShootLocationLike() {
		return shootLocationLike;
	}

	public void setShootLocationLike(String shootLocationLike) {
		this.shootLocationLike = shootLocationLike;
	}

	public String getMajorLike() {
		return majorLike;
	}

	public void setMajorLike(String majorLike) {
		this.majorLike = majorLike;
	}

	public String getMinorLike() {
		return minorLike;
	}

	public void setMinorLike(String minorLike) {
		this.minorLike = minorLike;
	}

	public String getThirdLevelLike() {
		return thirdLevelLike;
	}

	public void setThirdLevelLike(String thirdLevelLike) {
		this.thirdLevelLike = thirdLevelLike;
	}

	public String getScheduleIds() {
		return scheduleIds;
	}

	public void setScheduleIds(String scheduleIds) {
		this.scheduleIds = scheduleIds;
	}

	
}
