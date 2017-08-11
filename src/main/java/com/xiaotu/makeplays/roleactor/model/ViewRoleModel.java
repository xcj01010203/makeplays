package com.xiaotu.makeplays.roleactor.model;

/**
 * 场景角色信息
 * @author xuchangjian
 */
public class ViewRoleModel {

	public static final String TABLE_NAME = "tab_view_role";
	
	/**
	 * 场景角色ID
	 */
	private String viewRoleId;
	
	/**
	 * 场景角色名称
	 */
	private String viewRoleName;
	
	/**
	 * 场景角色简称
	 */
	private String shortName;
	
	/**
	 * 场景角色类型，详细信息见ViewRoleType枚举类
	 */
	private int viewRoleType;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 序列号
	 */
	private int sequence = 1;
	
	/**
	 * 是否是关注角色；默认不是
	 */
	private Boolean isAttentionRole;

	public Boolean getIsAttentionRole() {
		return isAttentionRole;
	}

	public void setIsAttentionRole(Boolean isAttentionRole) {
		this.isAttentionRole = isAttentionRole;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getViewRoleId() {
		return this.viewRoleId;
	}

	public void setViewRoleId(String viewRoleId) {
		this.viewRoleId = viewRoleId;
	}

	public String getViewRoleName() {
		return this.viewRoleName;
	}

	public void setViewRoleName(String viewRoleName) {
		this.viewRoleName = viewRoleName;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getViewRoleType() {
		return this.viewRoleType;
	}

	public void setViewRoleType(int viewRoleType) {
		this.viewRoleType = viewRoleType;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
