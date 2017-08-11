package com.xiaotu.makeplays.finance.controller.dto;

import java.util.List;

/**
 * 预算信息（财务科目结合货币信息）
 * @author xuchangjian 2016-8-4下午5:01:21
 */
public class BudgetInfoDto {

	/**
	 * 财务科目ID
	 */
	private String financeSubjId;
	
	/**
	 * 财务科目名称
	 */
	private String financeSubjName;
	
	/**
	 * 父科目ID
	 */
	private String financeSubjParentId;
	
	/**
	 * 级别
	 */
	private Integer level;
	
	/**
	 * 排列顺序
	 */
	private Integer sequence;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 是否有叶子节点
	 */
	private boolean hasChildren;
	
	/**
	 * 合同预算
	 */
	private Double contractBudget;
	
	/**
	 * 合同支出
	 */
	private Double contractPayed;
	
	/**
	 * 借款预算
	 */
	private Double loanBudget;
	
	/**
	 * 借款支出
	 */
	private Double loanPayed;
	
	/**
	 * 科目对应的货币信息列表
	 */
	private List<BudgetCurrencyDto> budgetCurrencyList;
	
	/**
	 * 子科目列表
	 */
	private List<BudgetInfoDto> children;

	public Double getContractPayed() {
		return this.contractPayed;
	}

	public void setContractPayed(Double contractPayed) {
		this.contractPayed = contractPayed;
	}

	public Double getLoanPayed() {
		return this.loanPayed;
	}

	public void setLoanPayed(Double loanPayed) {
		this.loanPayed = loanPayed;
	}

	public Integer getSequence() {
		return this.sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Double getContractBudget() {
		return this.contractBudget;
	}

	public void setContractBudget(Double contractBudget) {
		this.contractBudget = contractBudget;
	}

	public Double getLoanBudget() {
		return this.loanBudget;
	}

	public void setLoanBudget(Double loanBudget) {
		this.loanBudget = loanBudget;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public boolean isHasChildren() {
		return this.hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFinanceSubjId() {
		return this.financeSubjId;
	}

	public void setFinanceSubjId(String financeSubjId) {
		this.financeSubjId = financeSubjId;
	}

	public String getFinanceSubjName() {
		return this.financeSubjName;
	}

	public void setFinanceSubjName(String financeSubjName) {
		this.financeSubjName = financeSubjName;
	}

	public String getFinanceSubjParentId() {
		return this.financeSubjParentId;
	}

	public void setFinanceSubjParentId(String financeSubjParentId) {
		this.financeSubjParentId = financeSubjParentId;
	}

	public List<BudgetCurrencyDto> getBudgetCurrencyList() {
		return this.budgetCurrencyList;
	}

	public void setBudgetCurrencyList(List<BudgetCurrencyDto> budgetCurrencyList) {
		this.budgetCurrencyList = budgetCurrencyList;
	}

	public List<BudgetInfoDto> getChildren() {
		return this.children;
	}

	public void setChildren(List<BudgetInfoDto> children) {
		this.children = children;
	}
}
