package com.xiaotu.makeplays.sysrole.model;

/**
 * 用户和系统角色关联关系
 * @author xuchangjian 2016-5-12下午4:35:11
 */
public class UserRoleMapModel {

	public static String TABLE_NAME = "tab_user_role_map";
	
	/**
	 * id
	 */
	private String mapId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 系统角色ID
	 */
	private String roleId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
