package com.xiaotu.makeplays.view.controller.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 场景详细信息中下拉框内容列表信息，Map中的key为对象的ID，value为对象的名称，
 * 如果没有对应对象，比如cultureTypeList,key为枚举值，value为名称
 * @author xuchangjian
 */
public class ViewFilterDto implements Serializable {

	private static final long serialVersionUID = 141647399346014152L;
	
	/**
	 * 分组信息（A组、B组）
	 */
	private Map<String, String> groupList;
	
	/**
	 * 气氛列表
	 */
	private Map<String, String> atmosphereList;
	
	/**
	 * 季节
	 */
	private Map<Integer, String> seasonList;
	
	/**
	 * 拍摄状态
	 */
	private Map<Integer, String> shootStatusList;
	
	/**
	 * 内外景
	 */
	private List<String> siteList;
	
	/**
	 * 特殊提醒
	 */
	private List<String> specialRemindList;
	
	/**
	 * 场景地点
	 */
	private Map<String, String> viewLocationList;
	
	
	/**
	 * 主场景
	 */
	private Map<String, String> firstLocationList;
	
	
	/**
	 * 次场景
	 */
	private Map<String, String> secondLocationList;
	
	/**
	 * 三级场景
	 */
	private Map<String, String> thirdLocationList;
	
	/**
	 * 主要演员
	 */
	private Map<String, String> majorRoleList;
	
	/**
	 * 特约演员
	 */
	private Map<String, String> guestRoleList;
	
	/**
	 * 群众演员
	 */
	private Map<String, String> massesRoleList;
	
	/**
	 * 普通道具
	 */
	private Map<String, String> commonPropList;
	
	/**
	 * 特殊道具
	 */
	private Map<String, String> specialPropList;
	
	/**
	 * 文武戏
	 */
	private Map<Integer, String> cultureTypeList;
	
	/**
	 * 服装列表
	 */
	private Map<String, String> clotheList;
	
	/**
	 * 化妆列表
	 */
	private Map<String, String> makeupList;
	
	/**
	 * 拍摄地点信息
	 */
	private Map<String, String> shootLocationList;
	
	/**
	 * 广告信息
	 */
	private Map<String, String> advertInfoList;
	
	/**
	 * 地域
	 */
	private List<String> shootRegionList;
	
	/**
	 * 拍摄地点(地域)
	 */
	private List<Map<String, Object>> shootLocationRegionList;

	public List<String> getSpecialRemindList() {
		return specialRemindList;
	}

	public void setSpecialRemindList(List<String> specialRemindList) {
		this.specialRemindList = specialRemindList;
	}

	public Map<String, String> getSpecialPropList() {
		return this.specialPropList;
	}

	public void setSpecialPropList(Map<String, String> specialPropList) {
		this.specialPropList = specialPropList;
	}

	public Map<String, String> getShootLocationList() {
		return this.shootLocationList;
	}

	public void setShootLocationList(Map<String, String> shootLocationList) {
		this.shootLocationList = shootLocationList;
	}

	public Map<String, String> getGroupList() {
		return this.groupList;
	}

	public void setGroupList(Map<String, String> groupList) {
		this.groupList = groupList;
	}

	public Map<String, String> getAtmosphereList() {
		return this.atmosphereList;
	}

	public void setAtmosphereList(Map<String, String> atmosphereList) {
		this.atmosphereList = atmosphereList;
	}

	public Map<Integer, String> getSeasonList() {
		return this.seasonList;
	}

	public void setSeasonList(Map<Integer, String> seasonList) {
		this.seasonList = seasonList;
	}

	public Map<Integer, String> getShootStatusList() {
		return this.shootStatusList;
	}

	public void setShootStatusList(Map<Integer, String> shootStatusList) {
		this.shootStatusList = shootStatusList;
	}

	public List<String> getSiteList() {
		return this.siteList;
	}

	public void setSiteList(List<String> siteList) {
		this.siteList = siteList;
	}

	public Map<String, String> getViewLocationList() {
		return this.viewLocationList;
	}

	public void setViewLocationList(Map<String, String> viewLocationList) {
		this.viewLocationList = viewLocationList;
	}

	public Map<String, String> getFirstLocationList() {
		return this.firstLocationList;
	}

	public void setFirstLocationList(Map<String, String> firstLocationList) {
		this.firstLocationList = firstLocationList;
	}

	public Map<String, String> getSecondLocationList() {
		return this.secondLocationList;
	}

	public void setSecondLocationList(Map<String, String> secondLocationList) {
		this.secondLocationList = secondLocationList;
	}

	public Map<String, String> getThirdLocationList() {
		return this.thirdLocationList;
	}

	public void setThirdLocationList(Map<String, String> thirdLocationList) {
		this.thirdLocationList = thirdLocationList;
	}

	public Map<String, String> getMajorRoleList() {
		return this.majorRoleList;
	}

	public void setMajorRoleList(Map<String, String> majorRoleList) {
		this.majorRoleList = majorRoleList;
	}

	public Map<String, String> getGuestRoleList() {
		return this.guestRoleList;
	}

	public void setGuestRoleList(Map<String, String> guestRoleList) {
		this.guestRoleList = guestRoleList;
	}

	public Map<String, String> getMassesRoleList() {
		return this.massesRoleList;
	}

	public void setMassesRoleList(Map<String, String> massesRoleList) {
		this.massesRoleList = massesRoleList;
	}

	public Map<String, String> getCommonPropList() {
		return this.commonPropList;
	}

	public void setCommonPropList(Map<String, String> commonPropList) {
		this.commonPropList = commonPropList;
	}

	public Map<Integer, String> getCultureTypeList() {
		return this.cultureTypeList;
	}

	public void setCultureTypeList(Map<Integer, String> cultureTypeList) {
		this.cultureTypeList = cultureTypeList;
	}

	public Map<String, String> getClotheList() {
		return this.clotheList;
	}

	public void setClotheList(Map<String, String> clotheList) {
		this.clotheList = clotheList;
	}

	public Map<String, String> getMakeupList() {
		return this.makeupList;
	}

	public void setMakeupList(Map<String, String> makeupList) {
		this.makeupList = makeupList;
	}

	public Map<String, String> getAdvertInfoList() {
		return this.advertInfoList;
	}

	public void setAdvertInfoList(Map<String, String> advertInfoList) {
		this.advertInfoList = advertInfoList;
	}

	public List<String> getShootRegionList() {
		return shootRegionList;
	}

	public void setShootRegionList(List<String> shootRegionList) {
		this.shootRegionList = shootRegionList;
	}

	public List<Map<String, Object>> getShootLocationRegionList() {
		return shootLocationRegionList;
	}

	public void setShootLocationRegionList(
			List<Map<String, Object>> shootLocationRegionList) {
		this.shootLocationRegionList = shootLocationRegionList;
	}

}
