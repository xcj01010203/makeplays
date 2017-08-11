package com.xiaotu.makeplays.crew.model;

/**
 * 场景角色与用户关联表
 * @author lma
 *
 */
public class CrewRoleUserMapModel {
	public static final String TABLE_NAME="tab_crewRole_user_map";
	
	private String mapId ;
	private String userId;
	private String viewRoleId;
	private String crewId;
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getViewRoleId() {
		return viewRoleId;
	}
	public void setViewRoleId(String viewRoleId) {
		this.viewRoleId = viewRoleId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
	
}
