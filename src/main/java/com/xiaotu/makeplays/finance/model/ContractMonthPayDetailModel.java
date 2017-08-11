package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 合同按月支付薪酬明细
 * @author xuchangjian 2016-11-17上午11:22:32
 */
public class ContractMonthPayDetailModel {

	public static final String TABLE_NAME = "tab_contract_month_pay_detail";
	
	private String id;
	
	private String crewId;
	
	/**
	 * 合同ID
	 */
	private String contractId;
	
	/**
	 * 支付的薪酬月份，格式yyyy-MM
	 */
	private Date month;
	
	/**
	 * 薪酬开始计算日期
	 */
	private Date startDate;
	
	/**
	 * 薪酬结束计算日期
	 */
	private Date endDate;
	
	/**
	 * 薪酬
	 */
	private double money;
	
	/**
	 * 付款日期
	 */
	private Date payDate;
	
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

	public Date getMonth() {
		return this.month;
	}

	public void setMonth(Date month) {
		this.month = month;
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

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public Date getPayDate() {
		return this.payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
