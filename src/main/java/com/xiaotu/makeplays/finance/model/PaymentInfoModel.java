package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 付款单
 * @author xuchangjian 2016-8-9上午9:12:39
 */
public class PaymentInfoModel {
	
	public static final String TABLE_NAME="tab_payment_info";
	
	private String paymentId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 票据编号
	 */
	private String receiptNo;
	
	/**
	 * 付款日期
	 */
	private Date paymentDate;
	
	/**
	 * 收款人单位ID
	 */
	private String payeeId;
	
	/**
	 * 收款人单位名称
	 */
	private String payeeName;
	
	/**
	 * 收款人类型 详细信息见PayeeType枚举类
	 */
	private Integer payeeType;
	
	/**
	 * 合同ID
	 */
	private String contractId;
	
	/**
	 * 合同类型 详细信息见ContractType枚举类
	 */
	private Integer contractType;
	
	/**
	 * 币种ID
	 */
	private String currencyId;
	
	/**
	 * 合计金额
	 */
	private Double totalMoney;
	
	/**
	 * 财务付款方式
	 */
	private String paymentWay;
	
	/**
	 * 是否有发票
	 */
	private Boolean hasReceipt;
	
	/**
	 * 单据张数
	 */
	private Integer billCount;
	
	/**
	 * 经办人
	 */
	private String agent;
	
	/**
	 * 状态 详细信息见PaymentStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 是否收到票
	 */
	private Boolean ifReceiveBill;
	
	/**
	 * 票据种类，详细信息见BillType枚举类
	 */
	private Integer billType;
	
	/**
	 * 没有收到票时首页提醒时间
	 */
	private Date remindTime;
	
	/**
	 * 合同支付方式id
	 */
	private String contractWayId;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;
	
	/**
	 * 部门
	 */
	private String department;

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getAttpackId() {
		return attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public String getPaymentId() {
		return this.paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
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

	public Date getPaymentDate() {
		return this.paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPayeeId() {
		return this.payeeId;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}

	public String getPayeeName() {
		return this.payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public Integer getPayeeType() {
		return this.payeeType;
	}

	public void setPayeeType(Integer payeeType) {
		this.payeeType = payeeType;
	}

	public String getContractId() {
		return this.contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public Integer getContractType() {
		return this.contractType;
	}

	public void setContractType(Integer contractType) {
		this.contractType = contractType;
	}

	public String getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public Double getTotalMoney() {
		return this.totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getPaymentWay() {
		return this.paymentWay;
	}

	public void setPaymentWay(String paymentWay) {
		this.paymentWay = paymentWay;
	}

	public Boolean getHasReceipt() {
		return this.hasReceipt;
	}

	public void setHasReceipt(Boolean hasReceipt) {
		this.hasReceipt = hasReceipt;
	}

	public Integer getBillCount() {
		return this.billCount;
	}

	public void setBillCount(Integer billCount) {
		this.billCount = billCount;
	}

	public String getAgent() {
		return this.agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getIfReceiveBill() {
		return this.ifReceiveBill;
	}

	public void setIfReceiveBill(Boolean ifReceiveBill) {
		this.ifReceiveBill = ifReceiveBill;
	}

	public Integer getBillType() {
		return this.billType;
	}

	public void setBillType(Integer billType) {
		this.billType = billType;
	}

	public Date getRemindTime() {
		return this.remindTime;
	}

	public void setRemindTime(Date remindTime) {
		this.remindTime = remindTime;
	}

	public String getContractWayId() {
		return this.contractWayId;
	}

	public void setContractWayId(String contractWayId) {
		this.contractWayId = contractWayId;
	}
}
