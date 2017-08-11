package com.xiaotu.makeplays.sysrole.model;
/**
 * 系统角色信息表
 * @author lma
 *
 */
public class SysroleInfoModel {

	public static final String TABLE_NAME="tab_sysrole_info";
	
	/**
	 * 角色id
	 */
	private String roleId;
	
	/**
	 * 角色名称
	 */
	private String roleName;
	
	/**
	 * 角色描述
	 */
	private String roleDesc;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 上级角色id
	 */
	private String parentId;
	
	/**
	 * 是否可被评价
	 */
	private boolean canBeEvaluate;
	
	/**
	 * 角色级别
	 */
	private int level;
	
	/**
	 * 排列顺序
	 */
	private int orderNo;
	
	public boolean getCanBeEvaluate() {
		return this.canBeEvaluate;
	}
	public void setCanBeEvaluate(boolean canBeEvaluate) {
		this.canBeEvaluate = canBeEvaluate;
	}
	public String getCrewId() {
		return this.crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDesc() {
		return roleDesc;
	}
	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	
	
	
}
