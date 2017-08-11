package com.xiaotu.makeplays.finance.controller.filter;

/**
 * 借款单过滤条件 
 * @author xuchangjian 2016-10-12上午11:11:20
 */
public class LoanInfoFilter {

	/**
	 * 借款单ID，多个以逗号隔开
	 */
	private String loanIds;
	
	/**
	 * 财务科目ID，多个以逗号隔开
	 */
	private String financeSubjIds;
	
	/**
	 * 借款人，多个以逗号隔开
	 */
	private String payeeNames;
	
	/**
	 * 借款日期，多个以逗号隔开，格式：yyyy-MM-dd
	 */
	private String loanDates;
	
	/**
	 * 最小借款日期，格式：yyyy-MM-dd
	 */
	private String startLoanDate;

	/**
	 * 最大借款日期，格式：yyyy-MM-dd
	 */
	private String endLoanDate;
	
	/**
	 * 借款月份，格式：yyyy年MM月
	 */
	private String loanMonth;
	
	/**
	 * 记账人，多个以逗号隔开
	 */
	private String agents;
	
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
	 * 付款方式ID
	 */
	private String paymentWayId;

	public String getPaymentWayId() {
		return this.paymentWayId;
	}

	public void setPaymentWayId(String paymentWayId) {
		this.paymentWayId = paymentWayId;
	}

	public String getStartLoanDate() {
		return this.startLoanDate;
	}

	public void setStartLoanDate(String startLoanDate) {
		this.startLoanDate = startLoanDate;
	}

	public String getEndLoanDate() {
		return this.endLoanDate;
	}

	public void setEndLoanDate(String endLoanDate) {
		this.endLoanDate = endLoanDate;
	}
	
	public String getLoanIds() {
		return this.loanIds;
	}

	public void setLoanIds(String loanIds) {
		this.loanIds = loanIds;
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

	public String getLoanDates() {
		return this.loanDates;
	}

	public void setLoanDates(String loanDates) {
		this.loanDates = loanDates;
	}

	public String getLoanMonth() {
		return this.loanMonth;
	}

	public void setLoanMonth(String loanMonth) {
		this.loanMonth = loanMonth;
	}

	public String getAgents() {
		return this.agents;
	}

	public void setAgents(String agents) {
		this.agents = agents;
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
}
