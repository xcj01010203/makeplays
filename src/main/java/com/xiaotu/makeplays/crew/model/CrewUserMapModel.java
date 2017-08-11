package com.xiaotu.makeplays.crew.model;

import java.util.Date;

/**
 * 剧组用户关联关系表
 * @author lma
 *
 */
public class CrewUserMapModel {
	
	public static final String TABLE_NAME="tab_crew_user_map";
	
	private String  mapId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 角色ID，多个以逗号隔开
	 */
	private String roleId;
	
	/**
	 * 用户类型，详情见CrewUserType枚举类
	 */
	private Integer type;
	
	/**
	 * 用户在剧组中的状态。 详情见CrewUserStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 是否是默认剧组
	 */
	private boolean ifDefault;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public Date getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public boolean getIfDefault() {
		return this.ifDefault;
	}
	public void setIfDefault(boolean ifDefault) {
		this.ifDefault = ifDefault;
	}
	
}
