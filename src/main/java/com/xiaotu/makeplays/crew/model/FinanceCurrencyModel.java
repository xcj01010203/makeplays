package com.xiaotu.makeplays.crew.model;

import java.io.Serializable;

public class FinanceCurrencyModel implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * lma
	 */
	private String currencyId;
	private String currencyName;
	private String currencyCode;
	private int ifStandard;
	private int ifEnable;
	private double exchangeRate;
	private String crewId;
	private double budgetMoney;//预算金额
	private double settleAccountMoney;// 结算金额
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
	public int getIfStandard() {
		return ifStandard;
	}
	public void setIfStandard(int ifStandard) {
		this.ifStandard = ifStandard;
	}
	public int getIfEnable() {
		return ifEnable;
	}
	public void setIfEnable(int ifEnable) {
		this.ifEnable = ifEnable;
	}
	public double getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public double getBudgetMoney() {
		return budgetMoney;
	}
	public void setBudgetMoney(double budgetMoney) {
		this.budgetMoney = budgetMoney;
	}
	public double getSettleAccountMoney() {
		return settleAccountMoney;
	}
	public void setSettleAccountMoney(double settleAccountMoney) {
		this.settleAccountMoney = settleAccountMoney;
	}
	
	
}
