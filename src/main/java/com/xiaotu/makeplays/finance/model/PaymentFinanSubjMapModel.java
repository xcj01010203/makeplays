package com.xiaotu.makeplays.finance.model;

/**
 * 付款与财务科目关联信息表
 * @author xuchangjian 2016-8-9下午2:27:54
 */
public class PaymentFinanSubjMapModel {
	
	public static final String TABLE_NAME = "tab_payment_finanSubj_map";
	
	private String mapId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 付款单ID
	 */
	private String paymentId;
	
	/**
	 * 财务科目ID
	 */
	private String financeSubjId;
	
	/**
	 * 财务科目名称
	 */
	private String financeSubjName;
	
	/**
	 * 摘要
	 */
	private String summary;
	
	/**
	 * 金额
	 */
	private Double money;

	public String getFinanceSubjName() {
		return this.financeSubjName;
	}

	public void setFinanceSubjName(String financeSubjName) {
		this.financeSubjName = financeSubjName;
	}

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

	public String getPaymentId() {
		return this.paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getFinanceSubjId() {
		return this.financeSubjId;
	}

	public void setFinanceSubjId(String financeSubjId) {
		this.financeSubjId = financeSubjId;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}
}
