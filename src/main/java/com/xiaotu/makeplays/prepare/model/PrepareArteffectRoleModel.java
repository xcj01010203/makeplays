package com.xiaotu.makeplays.prepare.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @ClassName PrepareArteffectRoleModel
 * @Description 筹备期  美术视觉-角色
 * @author Administrator
 * @Date 2017年2月10日 上午10:26:58
 * @version 1.0.0
 */
public class PrepareArteffectRoleModel {
	public static final String TABLE_NAME = "tab_prepare_arteffect_role";
	
	private String id;
	
	private String role;//角色
	
	private String modelling;//造型
	
	private Date confirmDate;//确定日期
	
	private String status;//状态
	
	private String mark;//备注
	
	private String reviewer;//审核人
	
	private Timestamp createTime;//创建日期

	private String crewId;//剧组id
	
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

	public String getModelling() {
		return modelling;
	}

	public void setModelling(String modelling) {
		this.modelling = modelling;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
	
}
