package com.xiaotu.makeplays.authority.model;

public class RoleAuthMapModel {

	public static final String TABLE_NAME="tab_role_auth_map";
	
	
	private String mapId;
	private String roleId;
	private String authId;
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
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
	
	
}
