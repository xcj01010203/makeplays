package com.xiaotu.makeplays.user.model;

import java.util.Date;

/**
 * 剧组联系表
 * @author xuchangjian 2016-5-20上午9:20:53
 */
public class CrewContactModel {
	
	public static final String TABLE_NAME = "tab_crew_contact";
	
	/**
	 * 联系人id
	 */
	private String contactId;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 联系人姓名
	 */
	private String contactName;
	
	/**
	 * 联系人电话
	 */
	private String phone;
	
	/**
	 * 联系人性别，详情见Sex枚举类
	 */
	private Integer sex;
	
	/**
	 * 身份证件类型，详细信息见IdentityCardType枚举类
	 */
	private int identityCardType = 1;
	
	/**
	 * 身份证件号码
	 */
	private String identityCardNumber;
	
	/**
	 * 入组日期
	 */
	private Date enterDate;
	
	/**
	 * 离组日期
	 */
	private Date leaveDate;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 餐别，详细信息见MealType枚举类
	 */
	private Integer mealType;
	
	/**
	 * 是否公开到组
	 */
	private boolean ifOpen;
	
	/**
	 * 排列顺序
	 */
	private Integer sequence;
	
	/**
	 * 关联用户信息的id
	 */
	private String userId;

	public String getContactId() {
		return this.contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSex() {
		return this.sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public int getIdentityCardType() {
		return this.identityCardType;
	}

	public void setIdentityCardType(int identityCardType) {
		this.identityCardType = identityCardType;
	}

	public String getIdentityCardNumber() {
		return this.identityCardNumber;
	}

	public void setIdentityCardNumber(String identityCardNumber) {
		this.identityCardNumber = identityCardNumber;
	}

	public Date getEnterDate() {
		return this.enterDate;
	}

	public void setEnterDate(Date enterDate) {
		this.enterDate = enterDate;
	}

	public Date getLeaveDate() {
		return this.leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getMealType() {
		return this.mealType;
	}

	public void setMealType(Integer mealType) {
		this.mealType = mealType;
	}

	public boolean getIfOpen() {
		return this.ifOpen;
	}

	public void setIfOpen(boolean ifOpen) {
		this.ifOpen = ifOpen;
	}

	public Integer getSequence() {
		return this.sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
