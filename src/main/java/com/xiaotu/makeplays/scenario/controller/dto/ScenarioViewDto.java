package com.xiaotu.makeplays.scenario.controller.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 剧本中场景信息Dto
 * @author xuchangjian
 */
/**
 * @author Administrator
 *
 */
public class ScenarioViewDto implements Serializable {

	private static final long serialVersionUID = -6883393135445486881L;

	/**
	 * 集次
	 */
	private Integer seriesNo;
	
	/**
	 * 场次
	 */
	private String viewNo;
	
	/**
	 * 季节
	 */
	private Integer season;
	
	/**
	 * 气氛
	 */
	private String atmosphere;
	
	/**
	 * 内外景
	 */
	private String site;
	
	/**
	 * 拍摄地点
	 */
	private String shootLocation;
	
	/**
	 * 每一场的标题
	 */
	private String title;
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 主要内容
	 */
	private String mainContent;
	
	/**
	 * 页数
	 */
	private Double pageCount;
	
	/**
	 * 主要演员
	 */
	private List<String> majorRoleNameList;
	
	/**
	 * 特约演员
	 */
	private List<String> guestRoleNameList;
	
	/**
	 * 群众演员
	 */
	private List<String> massRoleNameList;
	
	/**
	 * 待定演员
	 */
	private List<String> toConfirmRoleNameList;
	
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
	 * 服装
	 */
	private String clothes;
	
	/**
	 * 化妆
	 */
	private String makeups;
	
	/**
	 * 道具
	 */
	private String props;
	
	/**
	 * 特殊道具
	 */
	private String specialProps;
	
	/**
	 * 文武戏
	 */
	private Integer viewType;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/*
	 * 商植
	 * 
	 */
	private String commercialImplants;
	/*
	 * 拍摄时间
	 * 
	 */
	private String shootTime;
	/*
	 * 完成状态
	 * 
	 */
	private Integer shootStatus;
	
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

	public List<String> getToConfirmRoleNameList() {
		return this.toConfirmRoleNameList;
	}

	public void setToConfirmRoleNameList(List<String> toConfirmRoleNameList) {
		this.toConfirmRoleNameList = toConfirmRoleNameList;
	}

	public String getCommercialImplants() {
		return commercialImplants;
	}

	public void setCommercialImplants(String commercialImplants) {
		this.commercialImplants = commercialImplants;
	}

	public String getShootTime() {
		return shootTime;
	}

	public void setShootTime(String shootTime) {
		this.shootTime = shootTime;
	}

	public Integer getShootStatus() {
		return shootStatus;
	}

	public void setShootStatus(Integer shootStatus) {
		this.shootStatus = shootStatus;
	}

	public String getSpecialProps() {
		return this.specialProps;
	}

	public void setSpecialProps(String specialProps) {
		this.specialProps = specialProps;
	}

	public Integer getViewType() {
		return this.viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
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

	public String getShootLocation() {
		return this.shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}

	public List<String> getGuestRoleNameList() {
		return this.guestRoleNameList;
	}

	public void setGuestRoleNameList(List<String> guestRoleNameList) {
		this.guestRoleNameList = guestRoleNameList;
	}

	public List<String> getMassRoleNameList() {
		return this.massRoleNameList;
	}

	public void setMassRoleNameList(List<String> massRoleNameList) {
		this.massRoleNameList = massRoleNameList;
	}

	public String getProps() {
		return this.props;
	}

	public void setProps(String props) {
		this.props = props;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMainContent() {
		return this.mainContent;
	}

	public void setMainContent(String mainContent) {
		this.mainContent = mainContent;
	}

	public Double getPageCount() {
		return this.pageCount;
	}

	public void setPageCount(Double pageCount) {
		this.pageCount = pageCount;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getMajorRoleNameList() {
		return this.majorRoleNameList;
	}

	public void setMajorRoleNameList(List<String> majorRoleNameList) {
		this.majorRoleNameList = majorRoleNameList;
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

	public Integer getSeason() {
		return this.season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}
}
