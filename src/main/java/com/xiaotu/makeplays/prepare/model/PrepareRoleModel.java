package com.xiaotu.makeplays.prepare.model;

import java.util.Date;

/**
 * @ClassName PrepareRoleModel
 * @Description 筹备期选角进度
 * @author Administrator
 * @Date 2017年2月10日 上午10:24:01
 * @version 1.0.0
 */
public class PrepareRoleModel {
	public static final String TABLE_NAME ="tab_prepare_role";
	
	private String id;
	
	private String role;//角色
	
	private String actor;//备选演员
	
	private String schedule;//进度
	
	private String content;//沟通内容
	
	private String mark;//备注
	
	private String parentId;//父id
	
	private String crewId;
	
	private Date createTime ;//创建时间
	
	
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	
}
