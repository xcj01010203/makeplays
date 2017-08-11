package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 财务设置信息
 * @author xuchangjian 2016-8-11下午6:20:50
 */
public class FinanceSettingModel {
	
	public static final String TABLE_NAME="tab_finance_setting_info";
	
	private String setId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 付款单编号是否按月重新开始
	 */
	private Boolean payStatus;
	
	/**
	 * 付款单编号是否分为有票无票
	 */
	private Boolean hasReceiptStatus;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 是否启用密码
	 */
	private boolean pwdStatus;
	
	/**
	 * 财务密码
	 */
	private String financePassword;
	
	/**
	 * 是否根据用户IP验证手机号，0：否，1：是
	 */
	private boolean ipStatus;
	
	/**
	 * 每月天数类型，详情见MonthDayType枚举类
	 */
	private int monthDayType = 2;
	
	/**
	 * 合同支付提前提醒天数
	 */
	private int contractAdvanceRemindDays = 5;
	
	/**
	 * 税的财务科目
	 */
	private String taxFinanSubjId;
	
	/**
	 * 税率
	 */
	private Double taxRate;

	public String getTaxFinanSubjId() {
		return this.taxFinanSubjId;
	}

	public void setTaxFinanSubjId(String taxFinanSubjId) {
		this.taxFinanSubjId = taxFinanSubjId;
	}

	public Double getTaxRate() {
		return this.taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public int getContractAdvanceRemindDays() {
		return this.contractAdvanceRemindDays;
	}

	public void setContractAdvanceRemindDays(int contractAdvanceRemindDays) {
		this.contractAdvanceRemindDays = contractAdvanceRemindDays;
	}

	public int getMonthDayType() {
		return this.monthDayType;
	}

	public void setMonthDayType(int monthDayType) {
		this.monthDayType = monthDayType;
	}

	public boolean getIpStatus() {
		return ipStatus;
	}

	public void setIpStatus(boolean ipStatus) {
		this.ipStatus = ipStatus;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getSetId() {
		return this.setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public Boolean getPayStatus() {
		return this.payStatus;
	}

	public void setPayStatus(Boolean payStatus) {
		this.payStatus = payStatus;
	}

	public Boolean getHasReceiptStatus() {
		return this.hasReceiptStatus;
	}

	public void setHasReceiptStatus(Boolean hasReceiptStatus) {
		this.hasReceiptStatus = hasReceiptStatus;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean getPwdStatus() {
		return this.pwdStatus;
	}

	public void setPwdStatus(boolean pwdStatus) {
		this.pwdStatus = pwdStatus;
	}

	public String getFinancePassword() {
		return this.financePassword;
	}

	public void setFinancePassword(String financePassword) {
		this.financePassword = financePassword;
	}
}
