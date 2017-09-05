package com.xiaotu.makeplays.authority.controller.dto;

import java.util.List;

/**
 * 用户权限信息
 * @author xuchangjian 2016-5-18下午3:45:34
 */
public class UserAuthDto {
	
	/**
	 * 权限ID
	 */
	private String authId;
	
	/**
	 * 父权限ID
	 */
	private String parentId;
	
	/**
	 * 权限名称
	 */
	private String authName;
	
	/**
	 * 排列顺序
	 */
	private Integer sequence;
	
	/**
	 * 权限所属平台类型
	 */
	private int authPlantform;
	
	/**
	 * 子权限信息
	 */
	private List<UserAuthDto> subAuthList;
	
	/**
	 * 是否区分读写操作
	 */
	private boolean differInRAndW;
	
	/**
	 * 是否拥有此权限
	 */
	private boolean hasAuth;
	
	/**
	 * 是否只读
	 */
	private boolean readonly;
	
	/**
	 * 多个用户拥有此权限的状态，0：都没有，1：都有，2：部分有
	 */
	private Integer hasAuthStatus;
	
	/**
	 * 多个用户是否只读状态，0：都否，1：都是，2：部分是
	 */
	private Integer readonlyStatus;

	public Integer getSequence() {
		return this.sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public int getAuthPlantform() {
		return this.authPlantform;
	}

	public void setAuthPlantform(int authPlantform) {
		this.authPlantform = authPlantform;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getAuthId() {
		return this.authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getAuthName() {
		return this.authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public List<UserAuthDto> getSubAuthList() {
		return this.subAuthList;
	}

	public void setSubAuthList(List<UserAuthDto> subAuthList) {
		this.subAuthList = subAuthList;
	}

	public boolean getDifferInRAndW() {
		return this.differInRAndW;
	}

	public void setDifferInRAndW(boolean differInRAndW) {
		this.differInRAndW = differInRAndW;
	}

	public boolean getHasAuth() {
		return this.hasAuth;
	}

	public void setHasAuth(boolean hasAuth) {
		this.hasAuth = hasAuth;
	}

	public boolean getReadonly() {
		return this.readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public Integer getHasAuthStatus() {
		return hasAuthStatus;
	}

	public void setHasAuthStatus(Integer hasAuthStatus) {
		this.hasAuthStatus = hasAuthStatus;
	}

	public Integer getReadonlyStatus() {
		return readonlyStatus;
	}

	public void setReadonlyStatus(Integer readonlyStatus) {
		this.readonlyStatus = readonlyStatus;
	}
}
