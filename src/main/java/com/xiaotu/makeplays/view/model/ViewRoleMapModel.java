package com.xiaotu.makeplays.view.model;

/**
 * 场景和场景角色关联关系模型
 * @author xuchangjian
 */
public class ViewRoleMapModel {

	public static final String TABLE_NAME = "tab_view_role_map";
	
	/**
	 * 关联关系ID
	 */
	private String mapId;
	
	/**
	 * 场景ID
	 */
	private String viewId;
	
	/**
	 * 场景角色ID
	 */
	private String viewRoleId;
	
	/**
	 * 场景角色数量
	 */
	private int roleNum;
	
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

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getViewRoleId() {
		return this.viewRoleId;
	}

	public void setViewRoleId(String viewRoleId) {
		this.viewRoleId = viewRoleId;
	}

	public int getRoleNum() {
		return this.roleNum;
	}

	public void setRoleNum(int roleNum) {
		this.roleNum = roleNum;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
