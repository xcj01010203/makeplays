package com.xiaotu.makeplays.authority.model;

/**
 * 用户和权限关联关系
 * @author xuchangjian 2016-5-12下午4:39:44
 */
public class UserAuthMapModel {
	
	public static final String TABLE_NAME = "tab_user_auth_map";
	
	private String mapId;
	
	/**
	 * 权限ID
	 */
	private String authId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 是否只读(只有当权限区分读写操作时才有效)
	 */
	private boolean readonly;

	public boolean getReadonly() {
		return this.readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getAuthId() {
		return this.authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
