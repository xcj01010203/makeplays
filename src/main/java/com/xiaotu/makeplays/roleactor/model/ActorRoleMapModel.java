package com.xiaotu.makeplays.roleactor.model;

/**
 * 演员与场景角色关联信息
 * @author xuchangjian 2016-7-12下午5:24:24
 */
public class ActorRoleMapModel {

	public final static String TABLE_NAME = "tab_actor_role_map";
	
	private String mapId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 演员ID
	 */
	private String actorId;
	
	/**
	 * 场景角色ID
	 */
	private String viewRoleId;

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getActorId() {
		return this.actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getViewRoleId() {
		return this.viewRoleId;
	}

	public void setViewRoleId(String viewRoleId) {
		this.viewRoleId = viewRoleId;
	}
	
}
