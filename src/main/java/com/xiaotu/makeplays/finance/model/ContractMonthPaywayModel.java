package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 合同按月支付表
 * @author xuchangjian 2016-11-17上午11:17:54
 */
public class ContractMonthPaywayModel {

	public static final String TABLE_NAME = "tab_contract_month_pay_way";
	
	private String id;
	
	private String crewId;
	
	/**
	 * 合同ID
	 */
	private String contractId;
	
	/**
	 * 月薪
	 */
	private double monthMoney;
	
	/**
	 * 付款开始日期
	 */
	private Date startDate;
	
	/**
	 * 付款结束日期
	 */
	private Date endDate;
	
	/**
	 * 每月发薪日
	 */
	private int monthPayDay;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 创建日期
	 */
	private Date createTime;
	
	/**
	 * 支付方式，见：ContractPayWay枚举类
	 */
	private int payWayType;

	public int getPayWayType() {
		return this.payWayType;
	}

	public void setPayWayType(int payWayType) {
		this.payWayType = payWayType;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getContractId() {
		return this.contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public double getMonthMoney() {
		return this.monthMoney;
	}

	public void setMonthMoney(double monthMoney) {
		this.monthMoney = monthMoney;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getMonthPayDay() {
		return this.monthPayDay;
	}

	public void setMonthPayDay(int monthPayDay) {
		this.monthPayDay = monthPayDay;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
