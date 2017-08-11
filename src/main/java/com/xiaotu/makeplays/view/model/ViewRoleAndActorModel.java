package com.xiaotu.makeplays.view.model;

import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;

/**
 * 场景角色信息的扩展类
 * @author xuchangjian 2016年8月4日上午9:30:35
 */
public class ViewRoleAndActorModel extends ViewRoleModel{

	
	/**
	 * 演员名称
	 */
	private String actorName;
	
	/**
	 * 演员的id
	 */
	private String actorId;
	
	/**
	 * 演员的数量
	 */
	private Integer roleNum;
	
	
	public String getActorName() {
		return actorName;
	}
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public Integer getRoleNum() {
		return roleNum;
	}
	public void setRoleNum(Integer roleNum) {
		this.roleNum = roleNum;
	}
	
}
