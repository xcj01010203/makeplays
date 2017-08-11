package com.xiaotu.makeplays.prepare.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @ClassName PrepareCrewPeopleModel
 * @Description 筹备期剧组人员
 * @author Administrator
 * @Date 2017年2月10日 上午10:25:37
 * @version 1.0.0
 */
public class PrepareCrewPeopleModel {
	public static final String TABLE_NAME = "tab_prepare_crewpeople";
	
	private String id;
	
	private String groupName;//组别
	
	private String duties;//职务
	
	private String name;//姓名
	
	private String phone;//电话
	
	private String reviewer;//审核人
	
	private Date confirmDate;//确认时间
	
	private Date arrivalTime;//到岗时间
	
	private Double payment;//薪酬
	
	private Timestamp createTime;//创建时间
	
	private String parentId;//父id
	
	private String crewId;

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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDuties() {
		return duties;
	}

	public void setDuties(String duties) {
		this.duties = duties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Double getPayment() {
		return payment;
	}

	public void setPayment(Double payment) {
		this.payment = payment;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
}
