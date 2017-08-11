package com.xiaotu.makeplays.view.controller.dto;

/**
 * 场次dto
 * @author xuchangjian
 */
public class ViewNoDto {

	
	/**
	 * 场次ID
	 */
	private String viewId;
	
	/**
	 * 场次号
	 */
	private String viewNo;
	
	/**
	 * 气氛
	 */
	private String atmosphereName;
	
	/**
	 * 内外景
	 */
	private String site;
	
	/**
	 * 该场次是否手动保存
	 */
	private boolean isManualSave = false;
	
	/**
	 * 场景标题
	 */
	private String title;
	
	/**
	 * 场景内容
	 */
	private String viewContent;
	
	
	/**
	 * 是否有关注演员
	 */
	private boolean hasFocusRole = false;
	
	/**
	 * 是否有未提取的角色
	 */
	private boolean hasNoGetRole;
	
	/**
	 * 是否已读
	 */
	private boolean isReaded;

	public boolean getIsReaded() {
		return this.isReaded;
	}

	public void setIsReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean getHasNoGetRole() {
		return this.hasNoGetRole;
	}

	public void setHasNoGetRole(boolean hasNoGetRole) {
		this.hasNoGetRole = hasNoGetRole;
	}

	public boolean isHasFocusRole() {
		return this.hasFocusRole;
	}

	public void setHasFocusRole(boolean hasFocusRole) {
		this.hasFocusRole = hasFocusRole;
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

	public String getViewContent() {
		return this.viewContent;
	}

	public void setViewContent(String viewContent) {
		this.viewContent = viewContent;
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

	public boolean getIsManualSave() {
		return this.isManualSave;
	}

	public void setIsManualSave(boolean isManualSave) {
		this.isManualSave = isManualSave;
	}
}
