package com.xiaotu.makeplays.view.controller.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * 场景DTO
 * @author xuchangjian
 */
public class ViewInfoDto implements Serializable {

	private static final long serialVersionUID = -3669510833795790922L;
	
	private String viewId;	//场景ID
	private int seriesNo;	//集次
	private String viewNo;		//场次
	private Integer seasonValue;	//季节的值
	private String seasonName; //季节名称
	private String atmosphereName;	//气氛
	private String site;	//内外景
	private double pageCount; // 页数
	
	private String firstLocation; // 主场景
	private String secondLocation; // 次场景
	private String thirdLocation; // 三级场景
	
	private String mainContent; // 主要内容
	private String majorActor; // 主要演员
	private String guestActor; // 特约演员
	private String massesActor; // 群众演员
	
	private String commonProps; // 道具
	private String specialProps; // 特殊道具
	
	private String clothes;	//服装
	private String makeups;	//化妆
	
	private Map<String, Integer> massesActorMap;//key：群众演员的名称；value：群众演员的人数
	
	
	private Integer type; // 文武戏
	private Integer typeValue;	//文武戏的值
	private String remark; // 备注
	private int shootStatus;// 拍摄状态
	private String shootStatusValue; //拍摄状态值
	private String title; // 标题
	private String content; // 剧本内容
	private String shootLocation;	//拍摄地点
	private String shootRegion; //地域
	
	private String crewId; //剧组id
	
	private boolean isManualSave; //是否手动保存
	
	private String specialRemind; //特殊提醒
	
	public String getSpecialRemind() {
		return specialRemind;
	}
	public void setSpecialRemind(String specialRemind) {
		this.specialRemind = specialRemind;
	}
	public Integer getTypeValue() {
		return this.typeValue;
	}
	public void setTypeValue(Integer typeValue) {
		this.typeValue = typeValue;
	}
	public Integer getSeasonValue() {
		return this.seasonValue;
	}
	public void setSeasonValue(Integer seasonValue) {
		this.seasonValue = seasonValue;
	}
	public String getShootLocation() {
		return this.shootLocation;
	}
	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}
	public String getShootStatusValue() {
		return this.shootStatusValue;
	}
	public void setShootStatusValue(String shootStatusValue) {
		this.shootStatusValue = shootStatusValue;
	}
	public String getSeasonName() {
		return this.seasonName;
	}
	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
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
	public boolean getIsManualSave() {
		return this.isManualSave;
	}
	public void setManualSave(boolean isManualSave) {
		this.isManualSave = isManualSave;
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
	public double getPageCount() {
		return this.pageCount;
	}
	public void setPageCount(double pageCount) {
		this.pageCount = pageCount;
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
	public String getMainContent() {
		return this.mainContent;
	}
	public void setMainContent(String mainContent) {
		this.mainContent = mainContent;
	}
	public String getMajorActor() {
		return this.majorActor;
	}
	public void setMajorActor(String majorActor) {
		this.majorActor = majorActor;
	}
	public String getGuestActor() {
		return this.guestActor;
	}
	public void setGuestActor(String guestActor) {
		this.guestActor = guestActor;
	}
	public String getMassesActor() {
		return this.massesActor;
	}
	public void setMassesActor(String massesActor) {
		this.massesActor = massesActor;
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
	public void setSpecialProps(String spacialProps) {
		this.specialProps = spacialProps;
	}
	public Map<String, Integer> getMassesActorMap() {
		return this.massesActorMap;
	}
	public void setMassesActorMap(Map<String, Integer> massesActorMap) {
		this.massesActorMap = massesActorMap;
	}
	
	public Integer getType() {
		return this.type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getShootStatus() {
		return this.shootStatus;
	}
	public void setShootStatus(int shootStatus) {
		this.shootStatus = shootStatus;
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
	public String getShootRegion() {
		return shootRegion;
	}
	public void setShootRegion(String shootRegion) {
		this.shootRegion = shootRegion;
	}
}
