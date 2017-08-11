package com.xiaotu.makeplays.crew.model;

import java.util.List;

public class FinanceBudgetModel {
	private String accountId;
	private String accountName;
	private int accountLevel;
	private String parentId;
	private int sequence;
	private String remark;
	private double exchangeRate;
	private String str_budgetId;
	private List<FinanceCurrencyModel> currencyList;
	private double total;//总预算
	private double percent;//预算比例(小数)
	private int hasChildFlag;
	private String flex;
	private int isChecked; // 1:选中
	private List<FinanceBudgetModel> childList;
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public int getAccountLevel() {
		return accountLevel;
	}
	public void setAccountLevel(int accountLevel) {
		this.accountLevel = accountLevel;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public double getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public String getStr_budgetId() {
		return str_budgetId;
	}
	public void setStr_budgetId(String str_budgetId) {
		this.str_budgetId = str_budgetId;
	}
	public List<FinanceCurrencyModel> getCurrencyList() {
		return currencyList;
	}
	public void setCurrencyList(List<FinanceCurrencyModel> currencyList) {
		this.currencyList = currencyList;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public int getHasChildFlag() {
		return hasChildFlag;
	}
	public void setHasChildFlag(int hasChildFlag) {
		this.hasChildFlag = hasChildFlag;
	}
	public String getFlex() {
		return flex;
	}
	public void setFlex(String flex) {
		this.flex = flex;
	}
	public int getIsChecked() {
		return isChecked;
	}
	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}
	public List<FinanceBudgetModel> getChildList() {
		return childList;
	}
	public void setChildList(List<FinanceBudgetModel> childList) {
		this.childList = childList;
	}
	
	
	
}
