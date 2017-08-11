package com.xiaotu.makeplays.prepare.model;

import java.sql.Timestamp;

/**
 * @ClassName PrepareOperateModel
 * @Description 筹备期  商务运营
 * @author Administrator
 * @Date 2017年2月10日 上午10:31:15
 * @version 1.0.0
 */
public class PrepareOperateModel {
	public static final String TABLE_NAME = "tab_prepare_operate";
	
	private String id;//主键
	
	private String operateType;//合作种类
	
	private String operateBrand;//品牌
	
	private String operateMode;//方式
	
	private Double operateCost;//合作费用
	
	private String contactName;//联系人名称
	
	private String phoneNumber;//联系电话
	
	private String mark;//备注
	
	private String personLiable;//负责人
	
	private String parentId;//父id
	
	private String crewId;//剧组id
	
	private Timestamp createTime;//创建时间

	
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

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getOperateBrand() {
		return operateBrand;
	}

	public void setOperateBrand(String operateBrand) {
		this.operateBrand = operateBrand;
	}

	public String getOperateMode() {
		return operateMode;
	}

	public void setOperateMode(String operateMode) {
		this.operateMode = operateMode;
	}

	public Double getOperateCost() {
		return operateCost;
	}

	public void setOperateCost(Double operateCost) {
		this.operateCost = operateCost;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
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

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
	
}
