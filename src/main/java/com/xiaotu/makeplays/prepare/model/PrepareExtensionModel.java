package com.xiaotu.makeplays.prepare.model;

import java.sql.Timestamp;

/**
 * @ClassName PrepareExtensionModel
 * @Description 筹备期 宣传进度
 * @author Administrator
 * @Date 2017年2月10日 上午10:29:17
 * @version 1.0.0
 */
public class PrepareExtensionModel {
	public static final String TABLE_NAME = "tab_prepare_extension";
	
	private String id;
	
	private String material;//素材
	
	private String type;//类型
	
	private String personLiable;//责任人
	
	private String reviewer;//审核人
	
	private Timestamp createTime;//创建时间

	private String crewId;//剧组id
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getPersonLiable() {
		return personLiable;
	}

	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
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
