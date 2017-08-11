package com.xiaotu.makeplays.crew.model;

import java.io.Serializable;
import java.util.List;

public class FinanceAccountDBGroupModel implements Serializable {

	/**
	 * LMA
	 */
	private String accountId;
	private String accountName;
	private double budget;
	private double pay;
	private int hasChild;
	private List<String> accountIdList;

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


	public double getBudget() {
		return budget;
	}


	public void setBudget(double budget) {
		this.budget = budget;
	}


	public double getPay() {
		return pay;
	}


	public void setPay(double pay) {
		this.pay = pay;
	}


	public int getHasChild() {
		return hasChild;
	}


	public void setHasChild(int hasChild) {
		this.hasChild = hasChild;
	}


	public List<String> getAccountIdList() {
		return accountIdList;
	}


	public void setAccountIdList(List<String> accountIdList) {
		this.accountIdList = accountIdList;
	}


	@Override
	public String toString() {
		return "FinanceAccountDBGroupModel [budget=" + budget + ", pay=" + pay
				+ ", accountName=" + accountName + "]";
	}

}
