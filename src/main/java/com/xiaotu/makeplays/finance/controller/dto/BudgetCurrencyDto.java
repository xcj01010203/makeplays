package com.xiaotu.makeplays.finance.controller.dto;

/**
 * 财务预算货币信息
 * @author xuchangjian 2016-8-4下午5:14:30
 */
public class BudgetCurrencyDto {

	/**
	 * 货币ID
	 */
	private String currencyId;
	
	/**
	 * 货币编码
	 */
	private String currencyCode;
	
	/**
	 * 货币名称
	 */
	private String currencyName;
	
	/**
	 * 汇率
	 */
	private Double exchangeRate;
	
	/**
	 * 是否本位币
	 */
	private boolean ifStandard;
	
	/**
	 * 和财务科目的关联关系ID
	 */
	private String mapId;
	
	/**
	 * 数量
	 */
	private Double amount;
	
	/**
	 * 预算金额
	 */
	private Double money;
	
	/**
	 * 支出金额
	 */
	private Double settleMoney;
	
	/**
	 * 单价
	 */
	private Double perPrice;
	
	/**
	 * 单位
	 */
	private String unitType;
	
	/**
	 * 有票支出金额
	 */
	private Double hasReceiptMoney;
	
	/**
	 * 无票支出金额
	 */
	private Double noReceiptMoney;

	public Double getHasReceiptMoney() {
		return hasReceiptMoney;
	}

	public void setHasReceiptMoney(Double hasReceiptMoney) {
		this.hasReceiptMoney = hasReceiptMoney;
	}

	public Double getNoReceiptMoney() {
		return noReceiptMoney;
	}

	public void setNoReceiptMoney(Double noReceiptMoney) {
		this.noReceiptMoney = noReceiptMoney;
	}

	public Double getSettleMoney() {
		return this.settleMoney;
	}

	public void setSettleMoney(Double settleMoney) {
		this.settleMoney = settleMoney;
	}

	public String getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencyName() {
		return this.currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Double getExchangeRate() {
		return this.exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public boolean isIfStandard() {
		return this.ifStandard;
	}

	public void setIfStandard(boolean ifStandard) {
		this.ifStandard = ifStandard;
	}

	public String getMapId() {
		return this.mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getPerPrice() {
		return this.perPrice;
	}

	public void setPerPrice(Double perPrice) {
		this.perPrice = perPrice;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

}
