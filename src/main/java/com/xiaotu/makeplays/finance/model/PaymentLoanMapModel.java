package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 付款单和借款单的关联关系
 * @author xuchangjian 2016-8-17下午5:14:56
 */
public class PaymentLoanMapModel {
	
	public static final String TABLE_NAME = "tab_payment_loan_map";

	private String mapId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 付款单ID
	 */
	private String paymentId;
	
	/**
	 * 借款单ID
	 */
	private String loanId;
	
	/**
	 * 预算金额
	 */
	private Double repaymentMoney;
	
	/**
	 * 借款余额
	 */
	private Double loanBalance;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getPaymentId() {
		return this.paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public Double getRepaymentMoney() {
		return this.repaymentMoney;
	}

	public void setRepaymentMoney(Double repaymentMoney) {
		this.repaymentMoney = repaymentMoney;
	}

	public Double getLoanBalance() {
		return this.loanBalance;
	}

	public void setLoanBalance(Double loanBalance) {
		this.loanBalance = loanBalance;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
