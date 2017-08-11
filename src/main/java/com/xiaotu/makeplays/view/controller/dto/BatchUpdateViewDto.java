package com.xiaotu.makeplays.view.controller.dto;

/**
 * 是否批量修改场景的DTO
 * 
 * @author wanrenyi 2016年12月7日下午2:05:42
 */
public class BatchUpdateViewDto extends StandardViewInfoDto {

	/**
	 * 是否修改主要内容
	 */
	private boolean cgMainContent;
	
	/**
	 * 是否修改备注
	 */
	private boolean cgRemark;
	
	/**
	 * 拍摄地点
	 */
	private boolean cgShootLocation;
	
	/**
	 *主场景
	 */
	private boolean cgLvlOneLocation;
	
	/**
	 * 次场景
	 */
	private boolean cgLvlTwoLocation;
	
	/**
	 * 三级场景
	 */
	private boolean cgLvlThreeLocation;
	
	/**主要演员
	 * 
	 */
	private boolean cgLeadingRoles;
	
	/**
	 * 特约演员
	 */
	private boolean cgGuestRoles;
	
	/**
	 * 群众演员
	 */
	private boolean cgMassesRoles;
	
	/**
	 * 气氛
	 */
	private boolean cgAtmosphereName;
	
	/**
	 * 内外景
	 */
	private boolean cgSite;
	
	/**
	 * 服装
	 */
	private boolean cgClothes;
	
	/**
	 * 化妆
	 */
	private boolean cgMakeups;
	
	/**
	 * 普通道具
	 */
	private boolean cgCommonProps;
	
	/**
	 * 特殊道具
	 */
	private boolean cgSpecialProps;
	
	/**
	 * 场景状态
	 */
	private boolean cgShootStatus;
	
	/**
	 * 是否更新特殊提醒
	 */
	private boolean cgSpecialRemark;
	
	/**
	 * 特殊提醒
	 */
	private String specialRemark;
	
	public boolean isCgSpecialRemark() {
		return cgSpecialRemark;
	}
	public void setCgSpecialRemark(boolean cgSpecialRemark) {
		this.cgSpecialRemark = cgSpecialRemark;
	}
	public String getSpecialRemark() {
		return specialRemark;
	}
	public void setSpecialRemark(String specialRemark) {
		this.specialRemark = specialRemark;
	}
	public boolean isCgShootStatus() {
		return cgShootStatus;
	}
	public void setCgShootStatus(boolean cgShootStatus) {
		this.cgShootStatus = cgShootStatus;
	}
	public boolean isCgMainContent() {
		return this.cgMainContent;
	}
	public void setCgMainContent(boolean cgMainContent) {
		this.cgMainContent = cgMainContent;
	}
	public boolean isCgRemark() {
		return this.cgRemark;
	}
	public void setCgRemark(boolean cgRemark) {
		this.cgRemark = cgRemark;
	}
	public boolean isCgShootLocation() {
		return this.cgShootLocation;
	}
	public void setCgShootLocation(boolean cgShootLocation) {
		this.cgShootLocation = cgShootLocation;
	}
	public boolean isCgLvlOneLocation() {
		return this.cgLvlOneLocation;
	}
	public void setCgLvlOneLocation(boolean cgLvlOneLocation) {
		this.cgLvlOneLocation = cgLvlOneLocation;
	}
	public boolean isCgLvlTwoLocation() {
		return this.cgLvlTwoLocation;
	}
	public void setCgLvlTwoLocation(boolean cgLvlTwoLocation) {
		this.cgLvlTwoLocation = cgLvlTwoLocation;
	}
	public boolean isCgLvlThreeLocation() {
		return this.cgLvlThreeLocation;
	}
	public void setCgLvlThreeLocation(boolean cgLvlThreeLocation) {
		this.cgLvlThreeLocation = cgLvlThreeLocation;
	}
	public boolean isCgLeadingRoles() {
		return this.cgLeadingRoles;
	}
	public void setCgLeadingRoles(boolean cgLeadingRoles) {
		this.cgLeadingRoles = cgLeadingRoles;
	}
	public boolean isCgGuestRoles() {
		return this.cgGuestRoles;
	}
	public void setCgGuestRoles(boolean cgGuestRoles) {
		this.cgGuestRoles = cgGuestRoles;
	}
	public boolean isCgMassesRoles() {
		return this.cgMassesRoles;
	}
	public void setCgMassesRoles(boolean cgMassesRoles) {
		this.cgMassesRoles = cgMassesRoles;
	}
	public boolean isCgAtmosphereName() {
		return this.cgAtmosphereName;
	}
	public void setCgAtmosphereName(boolean cgAtmosphereName) {
		this.cgAtmosphereName = cgAtmosphereName;
	}
	public boolean isCgSite() {
		return this.cgSite;
	}
	public void setCgSite(boolean cgSite) {
		this.cgSite = cgSite;
	}
	public boolean isCgClothes() {
		return this.cgClothes;
	}
	public void setCgClothes(boolean cgClothes) {
		this.cgClothes = cgClothes;
	}
	public boolean isCgMakeups() {
		return this.cgMakeups;
	}
	public void setCgMakeups(boolean cgMakeups) {
		this.cgMakeups = cgMakeups;
	}
	public boolean isCgCommonProps() {
		return this.cgCommonProps;
	}
	public void setCgCommonProps(boolean cgCommonProps) {
		this.cgCommonProps = cgCommonProps;
	}
	public boolean isCgSpecialProps() {
		return this.cgSpecialProps;
	}
	public void setCgSpecialProps(boolean cgSpecialProps) {
		this.cgSpecialProps = cgSpecialProps;
	}
}
