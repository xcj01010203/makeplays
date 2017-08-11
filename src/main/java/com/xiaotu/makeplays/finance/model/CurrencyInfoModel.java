package com.xiaotu.makeplays.finance.model;

/**
 * 货币信息
 * @author xuchangjian 2016-8-2上午9:48:08
 */
public class CurrencyInfoModel {
	
	public static final String TABLE_NAME="tab_currency_info";
	
	private String id;

	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 编码
	 */
	private String code;
	
	/**
	 * 是否本位币
	 */
	private boolean ifStandard;
	
	/**
	 * 是否启用
	 */
	private boolean ifEnable;
	
	/**
	 * 汇率
	 */
	private double exchangeRate;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean getIfStandard() {
		return this.ifStandard;
	}

	public void setIfStandard(boolean ifStandard) {
		this.ifStandard = ifStandard;
	}

	public boolean getIfEnable() {
		return this.ifEnable;
	}

	public void setIfEnable(boolean ifEnable) {
		this.ifEnable = ifEnable;
	}

	public double getExchangeRate() {
		return this.exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
}
