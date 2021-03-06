package com.xiaotu.makeplays.authority.controller.dto;

import java.util.List;

/**
 * @类名：CrewAuthDto.java
 * @作者：李晓平
 * @时间：2017年2月10日 下午2:50:17
 * @描述：剧组权限信息
 */
public class CrewAuthDto {
	
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
	private List<CrewAuthDto> subAuthList;
	
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

	public List<CrewAuthDto> getSubAuthList() {
		return this.subAuthList;
	}

	public void setSubAuthList(List<CrewAuthDto> subAuthList) {
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
}
