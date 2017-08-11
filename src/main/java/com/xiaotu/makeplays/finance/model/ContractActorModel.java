package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 演员合同信息表
 * @author xuchangjian 2016-8-2上午11:00:10
 */
public class ContractActorModel {

	public static final String TABLE_NAME = "tab_contract_actor";

	private String contractId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 合同编号
	 */
	private String contractNo;
	
	/**
	 * 合同日期
	 */
	private Date contractDate;
	
	/**
	 * 演员姓名
	 */
	private String actorName;
	
	/**
	 * 角色名称
	 */
	private String roleName;
	
	/**
	 * 联系电话
	 */
	private String phone;
	
	/**
	 * 身份证件类型，详情见IdentityCardType枚举类
	 */
	private Integer identityCardType;
	
	/**
	 * 身份证号码
	 */
	private String identityCardNumber;
	
	/**
	 * 合同开始日期
	 */
	private Date startDate;
	
	/**
	 * 合同结束日期
	 */
	private Date endDate;
	
	/**
	 * 货币ID
	 */
	private String currencyId;
	
	/**
	 * 总金额
	 */
	private Double totalMoney;
	
	/**
	 * 个税
	 */
	private String tax;
	
	/**
	 * 支付条件
	 */
	private String paymentTerm;
	
	/**
	 * 银行名称
	 */
	private String bankName;
	
	/**
	 * 银行账户名称
	 */
	private String bankAccountName;
	
	/**
	 * 银行账户账号
	 */
	private String bankAccountNumber;
	
	/**
	 * 支付方式，详细信息见ContractPayWay枚举类
	 */
	private Integer payWay;
	
	/**
	 * 财务科目id
	 */
	private String financeSubjId;
	
	/**
	 * 财务科目名称
	 */
	private String financeSubjName;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 用户自定的合同编号
	 */
	private String customContractNo;

	public String getCustomContractNo() {
		return customContractNo;
	}

	public void setCustomContractNo(String customContractNo) {
		this.customContractNo = customContractNo;
	}

	public Integer getIdentityCardType() {
		return this.identityCardType;
	}

	public void setIdentityCardType(Integer identityCardType) {
		this.identityCardType = identityCardType;
	}

	public String getFinanceSubjName() {
		return this.financeSubjName;
	}

	public void setFinanceSubjName(String financeSubjName) {
		this.financeSubjName = financeSubjName;
	}

	public String getAttpackId() {
		return this.attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public String getContractId() {
		return this.contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getActorName() {
		return this.actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIdentityCardNumber() {
		return this.identityCardNumber;
	}

	public void setIdentityCardNumber(String identityCardNumber) {
		this.identityCardNumber = identityCardNumber;
	}

	public Date getContractDate() {
		return this.contractDate;
	}

	public void setContractDate(Date contractDate) {
		this.contractDate = contractDate;
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

	public String getTax() {
		return this.tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getPaymentTerm() {
		return this.paymentTerm;
	}

	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccountName() {
		return this.bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccountNumber() {
		return this.bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public Integer getPayWay() {
		return this.payWay;
	}

	public void setPayWay(Integer payWay) {
		this.payWay = payWay;
	}

	public String getFinanceSubjId() {
		return this.financeSubjId;
	}

	public void setFinanceSubjId(String financeSubjId) {
		this.financeSubjId = financeSubjId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
