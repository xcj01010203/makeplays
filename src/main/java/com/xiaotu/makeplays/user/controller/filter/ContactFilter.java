package com.xiaotu.makeplays.user.controller.filter;

/**
 * 剧组联系表高级查询条件对象
 * @author xuchangjian 2016年8月8日上午10:54:09
 */
public class ContactFilter {
	//联系人id
	private String contactId;
	
	/**
	 * 联系人姓名
	 */
	private String contactName;
	
	/**
	 * 联系人性别;0：女；1：男
	 */
	private Integer sex;
	
	/**
	 * 部门
	 */
	private String departmentIds;
	
	/**
	 * 职务
	 */
	private String sysRoleIds;
	
	/**
	 * 1-身份证  2-护照  3-台胞证  4-军官证   5-其他
	 */
	private Integer identityCardType;
	
	/**
	 * 入组时间
	 */
	private String enterDate;
	
	/**
	 * 离组时间
	 */
	private String leaveDate;
	
	/**
	 * 餐别。1：常规；2：回民 3：素餐
	 */
	private Integer mealType;
	
	/**
	 * 宾馆
	 */
	private String hotel;
	
	/**
	 * 是否公开到组
	 */
	private Boolean ifOpen;
	
	/**
	 * 查询来源
	 */
	private String sourceFrom;

	public String getSourceFrom() {
		return sourceFrom;
	}

	public void setSourceFrom(String sourceFrom) {
		this.sourceFrom = sourceFrom;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public Integer getSex() {
		return this.sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getDepartmentIds() {
		return this.departmentIds;
	}

	public void setDepartmentIds(String departmentIds) {
		this.departmentIds = departmentIds;
	}

	public String getSysRoleIds() {
		return this.sysRoleIds;
	}

	public void setSysRoleIds(String sysRoleIds) {
		this.sysRoleIds = sysRoleIds;
	}

	public Integer getIdentityCardType() {
		return this.identityCardType;
	}

	public void setIdentityCardType(Integer identityCardType) {
		this.identityCardType = identityCardType;
	}

	public String getEnterDate() {
		return this.enterDate;
	}

	public void setEnterDate(String enterDate) {
		this.enterDate = enterDate;
	}

	public String getLeaveDate() {
		return this.leaveDate;
	}

	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}

	public Integer getMealType() {
		return this.mealType;
	}

	public void setMealType(Integer mealType) {
		this.mealType = mealType;
	}

	public String getHotel() {
		return this.hotel;
	}

	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public Boolean getIfOpen() {
		return this.ifOpen;
	}

	public void setIfOpen(Boolean ifOpen) {
		this.ifOpen = ifOpen;
	}
}
