package com.xiaotu.makeplays.finance.model;

/**
 * 货币和财务科目关联关系表
 * @author xuchangjian 2016-8-2上午10:35:17
 */
public class FinanSubjCurrencyMapModel {
	
	public static final String TABLE_NAME = "tab_finanSubj_currency_map";

	private String mapId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 财务科目ID
	 */
	private String financeSubjId;
	
	/**
	 * 货币ID
	 */
	private String currencyId;
	
	/**
	 * 预算金额
	 */
	private double money;
	
	/**
	 * 数量
	 */
	private double amount;
	
	/**
	 * 单价
	 */
	private double perPrice;
	
	/**
	 * 单位类型
	 */
	private String unitType;

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getFinanceSubjId() {
		return this.financeSubjId;
	}

	public void setFinanceSubjId(String financeSubjId) {
		this.financeSubjId = financeSubjId;
	}

	public String getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPerPrice() {
		return this.perPrice;
	}

	public void setPerPrice(double perPrice) {
		this.perPrice = perPrice;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

}
