package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 借款信息
 * @author xuchangjian 2016-8-2下午2:21:22
 */
public class LoanInfoModel  {
	
	public static final String TABLE_NAME="tab_loan_info";

	private String loanId;

	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 票据编号
	 */
	private String receiptNo;
	
	/**
	 * 借款日期
	 */
	private Date loanDate;
	
	/**
	 * 借款人单位ID
	 */
	private String loanerId;
	
	/**
	 * 借款人单位名称
	 */
	private String payeeName;
	
	/**
	 * 借款人类型，详细信息见LoanerType枚举类
	 */
	private Integer loanerType;
	
	/**
	 * 摘要
	 */
	private String summary;
	
	/**
	 * 金额
	 */
	private double money;
	
	/**
	 * 货币ID
	 */
	private String currencyId;  
	
	/**
	 * 付款方式，详细信息见LoanPaymentWay枚举类
	 */
	private int paymentWay = 1;
	
	/**
	 * 经办人
	 */
	private String agent;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 财务科目ID
	 */
	private String financeSubjId;
	
	/**
	 * 财务科目名称
	 */
	private String financeSubjName;
	
	/**
	 * 附件包id
	 */
	private String attpackId;

	public String getAttpackId() {
		return attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public String getFinanceSubjName() {
		return this.financeSubjName;
	}

	public void setFinanceSubjName(String financeSubjName) {
		this.financeSubjName = financeSubjName;
	}

	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getReceiptNo() {
		return this.receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Date getLoanDate() {
		return this.loanDate;
	}

	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	public String getLoanerId() {
		return this.loanerId;
	}

	public void setLoanerId(String loanerId) {
		this.loanerId = loanerId;
	}

	public String getPayeeName() {
		return this.payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public Integer getLoanerType() {
		return this.loanerType;
	}

	public void setLoanerType(Integer loanerType) {
		this.loanerType = loanerType;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public int getPaymentWay() {
		return this.paymentWay;
	}

	public void setPaymentWay(int paymentWay) {
		this.paymentWay = paymentWay;
	}

	public String getAgent() {
		return this.agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getFinanceSubjId() {
		return this.financeSubjId;
	}

	public void setFinanceSubjId(String financeSubjId) {
		this.financeSubjId = financeSubjId;
	}
}
