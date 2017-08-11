package com.xiaotu.makeplays.crew.model;
/**
 * @author lma
 *
 */
public class FinanceBudgetDBModel {
	
	private String accountId;
	private String accountName;
	private int accountLevel;
	private String parentId;
	private int sequence;
	private String remark;
	private String currencyId;
	private String currencyName;
	private String currencyCode;
	private double money;
	private double exchangeRate;
	private int ifStandard;
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
	public String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public double getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public int getIfStandard() {
		return ifStandard;
	}
	public void setIfStandard(int ifStandard) {
		this.ifStandard = ifStandard;
	}
	
	
}
