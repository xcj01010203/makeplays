package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 职员合同信息表
 * @author xuchangjian 2016-8-2下午1:51:28
 */
public class ContractWorkerModel {
	
	public static final String TABLE_NAME="tab_contract_worker";
	
	/**
	 * 合同ID
	 */
	private String contractId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 合同号
	 */
	private String contractNo;
	
	/**
	 * 合同签订日期
	 */
	private Date contractDate;
	
	/**
	 * 职员姓名
	 */
	private String workerName;
	
	/**
	 * 部门职务
	 */
	private String department;
	
	/**
	 * 联系电话
	 */
	private String phone;
	
	/**
	 * 身份证件类型，详情见IdentityCardType枚举类
	 */
	private Integer identityCardType;
	
	/**
	 * 身份证件号码
	 */
	private String identityCardNumber;
	
	/**
	 * 入组日期
	 */
	private Date enterDate;
	
	/**
	 * 离组日期
	 */
	private Date leaveDate;
	
	/**
	 * 货币ID
	 */
	private String currencyId;
	
	/**
	 * 总金额
	 */
	private Double totalMoney;
	
	/**
	 * 月薪总额
	 */
	private String monthlySalary;
	
	/**
	 * 个税
	 */
	private String tax;
	
	/**
	 * 实付月薪
	 */
	private String paidinms;
	
	/**
	 * 日薪
	 */
	private String dailySalary;
	
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
	 * 银行账号
	 */
	private String bankAccountNumber;
	
	/**
	 * 支付方式，详细信息见ContractPayWay枚举类
	 */
	private Integer payWay;
	
	/**
	 * 财务科目ID
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
	 * 用户自定义合同编号
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

	public Date getContractDate() {
		return this.contractDate;
	}

	public void setContractDate(Date contractDate) {
		this.contractDate = contractDate;
	}

	public String getWorkerName() {
		return this.workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
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

	public Date getEnterDate() {
		return this.enterDate;
	}

	public void setEnterDate(Date enterDate) {
		this.enterDate = enterDate;
	}

	public Date getLeaveDate() {
		return this.leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
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

	public String getMonthlySalary() {
		return this.monthlySalary;
	}

	public void setMonthlySalary(String monthlySalary) {
		this.monthlySalary = monthlySalary;
	}

	public String getTax() {
		return this.tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getPaidinms() {
		return this.paidinms;
	}

	public void setPaidinms(String paidinms) {
		this.paidinms = paidinms;
	}

	public String getDailySalary() {
		return this.dailySalary;
	}

	public void setDailySalary(String dailySalary) {
		this.dailySalary = dailySalary;
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
