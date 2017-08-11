package com.xiaotu.makeplays.crew.service;

import java.util.List;

public class FinanceBudgetDataModel {

	private List playCurrencyList;//剧组所有币种列表
	private List budgetList;//剧组所有预算

	public List getPlayCurrencyList() {
		return playCurrencyList;
	}

	public void setPlayCurrencyList(List playCurrencyList) {
		this.playCurrencyList = playCurrencyList;
	}

	public List getBudgetList() {
		return budgetList;
	}

	public void setBudgetList(List budgetList) {
		this.budgetList = budgetList;
	}

}
