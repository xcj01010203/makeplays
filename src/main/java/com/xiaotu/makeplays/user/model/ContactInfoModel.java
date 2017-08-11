package com.xiaotu.makeplays.user.model;

public class ContactInfoModel {
	
	public static final String TABLE_NAME="tab_common_contactor";
	
	private String contactorId;
	private String crewId;
	private String name;
	private String job;
	private String groupId;
	private String phone;
	private String createTime;
	public String getContactorId() {
		return contactorId;
	}
	public void setContactorId(String contactorId) {
		this.contactorId = contactorId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
