package com.xiaotu.makeplays.roleactor.controller.filter;

/**
 * 角色表高级查询条件
 * @author xuchangjian 2016-7-13下午2:40:03
 */
public class ViewRoleFilter {

	/**
	 * 角色名称
	 */
	private String viewRoleName;
	
	/**
	 * 演员类型
	 */
	private Integer viewRoleType;
	
	/**
	 * 最小场数
	 */
	private Integer minViewCount;
	
	/**
	 * 最大场数
	 */
	private Integer maxViewCount;
	
	/**
	 * 最小完成%
	 */
	private Integer minFinished;
	
	/**
	 * 最大完成%
	 */
	private Integer maxFinished;

	public String getViewRoleName() {
		return this.viewRoleName;
	}

	public void setViewRoleName(String viewRoleName) {
		this.viewRoleName = viewRoleName;
	}

	public Integer getViewRoleType() {
		return this.viewRoleType;
	}

	public void setViewRoleType(Integer viewRoleType) {
		this.viewRoleType = viewRoleType;
	}

	public Integer getMinViewCount() {
		return this.minViewCount;
	}

	public void setMinViewCount(Integer minViewCount) {   
		this.minViewCount = minViewCount;
	}

	public Integer getMaxViewCount() {
		return this.maxViewCount;
	}

	public void setMaxViewCount(Integer maxViewCount) {
		this.maxViewCount = maxViewCount;
	}

	public Integer getMinFinished() {
		return minFinished;
	}

	public void setMinFinished(Integer minFinished) {
		this.minFinished = minFinished;
	}

	public Integer getMaxFinished() {
		return maxFinished;
	}

	public void setMaxFinished(Integer maxFinished) {
		this.maxFinished = maxFinished;
	}
}
