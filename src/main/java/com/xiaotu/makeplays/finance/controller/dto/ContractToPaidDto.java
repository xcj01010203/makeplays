package com.xiaotu.makeplays.finance.controller.dto;

import java.util.Date;
import java.util.List;

/**
 * 
 * 合同待付信息
 * @author Administrator
 *
 */
public class ContractToPaidDto {
	
	private String id;//主键
	private Date paidDate;//待付日期
	private String contractNo;//合同号
	private String summary;//摘要
	private String financeSubjectName;//财务科目
	private String status;//状态
	private String paymentNo;//付款单号
	private List<ContractToPaidCurrencyDto> moneyList;//待付金额信息
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Date getPaidDate() {
		return paidDate;
	}
	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getFinanceSubjectName() {
		return financeSubjectName;
	}
	public void setFinanceSubjectName(String financeSubjectName) {
		this.financeSubjectName = financeSubjectName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPaymentNo() {
		return paymentNo;
	}
	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}
	public List<ContractToPaidCurrencyDto> getMoneyList() {
		return moneyList;
	}
	public void setMoneyList(List<ContractToPaidCurrencyDto> moneyList) {
		this.moneyList = moneyList;
	}
	
}
