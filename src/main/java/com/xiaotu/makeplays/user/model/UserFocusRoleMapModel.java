package com.xiaotu.makeplays.user.model;

/**
 * 用户和演员角色的关联关系
 * 该关联为用户和关注的演员的关联关系
 * @author xuchangjian
 */
public class UserFocusRoleMapModel {

	public static final String TABLE_NAME = "tab_user_focusrole_map";
	
	private String mapId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 场景角色Id
	 */
	private String viewRoleId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

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

	public String getViewRoleId() {
		return this.viewRoleId;
	}

	public void setViewRoleId(String viewRoleId) {
		this.viewRoleId = viewRoleId;
	}

}
