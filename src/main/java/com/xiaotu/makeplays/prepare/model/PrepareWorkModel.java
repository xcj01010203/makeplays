package com.xiaotu.makeplays.prepare.model;

import java.util.Date;

/**
 * @ClassName PrepareWorkModel
 * @Description 筹备期  办公筹备
 * @author Administrator
 * @Date 2017年2月10日 上午10:30:18
 * @version 1.0.0
 */
public class PrepareWorkModel {
	public static final String TABLE_NAME = "tab_prepare_work";
	
	private String id;//id
	
	private String type;//类型
	
	private String  purpose;//用途   工作
	
	private String schedule;//进度
	
	private String personLiable;//负责人
	
	private String parentId;//父id
	
	private Date createTime;//创建时间

	private String crewId;//剧组id
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getPersonLiable() {
		return personLiable;
	}

	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	
}
