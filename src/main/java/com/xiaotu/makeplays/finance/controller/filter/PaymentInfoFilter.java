package com.xiaotu.makeplays.finance.controller.filter;

/**
 * 付款单过滤条件
 * @author xuchangjian 2016-10-12上午10:39:04
 */
public class PaymentInfoFilter {

	/**
	 * 付款单ID，多个以逗号隔开
	 */
	private String paymentIds;
	
	/**
	 * 财务科目ID，多个以逗号隔开
	 */
	private String financeSubjIds;
	
	/**
	 * 付款人，多个以逗号隔开
	 */
	private String payeeNames;
	
	/**
	 * 借款人名称
	 */
	private String loanerName;
	
	/**
	 * 付款日期，多个以逗号隔开
	 */
	private String paymentDates;
	
	/**
	 * 最小付款日期，格式：yyyy-MM-dd
	 */
	private String startPaymentDate;
	
	/**
	 * 最大付款日期，格式：yyyy-MM-dd
	 */
	private String endPaymentDate;
	
	/**
	 * 付款月份，格式：yyyy年MM月
	 */
	private String paymentMonth;
	
	/**
	 * 记账人，多个以逗号隔开
	 */
	private String agents;
	
	/**
	 * 部门，多个以逗号隔开
	 */
	private String department;
	
	/**
	 * 有无发票
	 */
	private Boolean hasReceipt;
	
	/**
	 * 结算状态，详情见PaymentStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 摘要
	 */
	private String summary;
	
	/**
	 * 最小金额
	 */
	private Double minMoney;
	
	/**
	 * 最大金额
	 */
	private Double maxMoney;
	
	/**
	 * 支付方式ID
	 */
	private String paymentWayId;
	
	/**
	 * 单据类型
	 */
	private Integer billType;
	
	/**
	 * 是否是查询财务科目支付明细
	 */
	private boolean isQueryFinanceSubjPayment;
	
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Integer getBillType() {
		return this.billType;
	}

	public void setBillType(Integer billType) {
		this.billType = billType;
	}

	public String getLoanerName() {
		return this.loanerName;
	}

	public void setLoanerName(String loanerName) {
		this.loanerName = loanerName;
	}

	public String getPaymentWayId() {
		return this.paymentWayId;
	}

	public void setPaymentWayId(String paymentWayId) {
		this.paymentWayId = paymentWayId;
	}

	public String getPaymentIds() {
		return this.paymentIds;
	}

	public void setPaymentIds(String paymentIds) {
		this.paymentIds = paymentIds;
	}

	public String getFinanceSubjIds() {
		return this.financeSubjIds;
	}

	public void setFinanceSubjIds(String financeSubjIds) {
		this.financeSubjIds = financeSubjIds;
	}

	public String getPayeeNames() {
		return this.payeeNames;
	}

	public void setPayeeNames(String payeeNames) {
		this.payeeNames = payeeNames;
	}

	public String getPaymentDates() {
		return this.paymentDates;
	}

	public void setPaymentDates(String paymentDates) {
		this.paymentDates = paymentDates;
	}

	public String getStartPaymentDate() {
		return this.startPaymentDate;
	}

	public void setStartPaymentDate(String startPaymentDate) {
		this.startPaymentDate = startPaymentDate;
	}

	public String getEndPaymentDate() {
		return this.endPaymentDate;
	}

	public void setEndPaymentDate(String endPaymentDate) {
		this.endPaymentDate = endPaymentDate;
	}

	public String getPaymentMonth() {
		return this.paymentMonth;
	}

	public void setPaymentMonth(String paymentMonth) {
		this.paymentMonth = paymentMonth;
	}

	public String getAgents() {
		return this.agents;
	}

	public void setAgents(String agents) {
		this.agents = agents;
	}

	public Boolean getHasReceipt() {
		return this.hasReceipt;
	}

	public void setHasReceipt(Boolean hasReceipt) {
		this.hasReceipt = hasReceipt;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Double getMinMoney() {
		return this.minMoney;
	}

	public void setMinMoney(Double minMoney) {
		this.minMoney = minMoney;
	}

	public Double getMaxMoney() {
		return this.maxMoney;
	}

	public void setMaxMoney(Double maxMoney) {
		this.maxMoney = maxMoney;
	}

	public boolean isQueryFinanceSubjPayment() {
		return isQueryFinanceSubjPayment;
	}

	public void setQueryFinanceSubjPayment(boolean isQueryFinanceSubjPayment) {
		this.isQueryFinanceSubjPayment = isQueryFinanceSubjPayment;
	}
}
