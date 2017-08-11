package com.xiaotu.makeplays.finance.controller.filter;

/**
 * 收款单过滤条件
 * @author xuchangjian 2016-10-12上午10:52:10
 */
public class CollectionInfoFilter {

	/**
	 * 收款单ID，多个以逗号隔开
	 */
	private String collectionIds;
	
	/**
	 * 付款人，多个以逗号隔开
	 */
	private String otherUnits;
	
	/**
	 * 收款日期，多个以逗号隔开，格式：yyyy-MM-dd
	 */
	private String collectionDates;
	
	/**
	 * 最小收款日期，格式：yyyy-MM-dd
	 */
	private String startCollectionDate;
	
	/**
	 * 最大收款日期，格式：yyyy-MM-dd
	 */
	private String endCollectionDate;
	
	/**
	 * 收款月份，格式：yyyy年MM月
	 */
	private String collectionMonth;
	
	/**
	 * 记账人，多个以逗号隔开
	 */
	private String agents;
	
	/**
	 * 摘要
	 */
	private String summary;
	
	/**
	 * 最小金额
	 */
	private Double minMoney;
	
	/**
	 * 最大金额
	 */
	private Double maxMoney;
	
	/**
	 * 付款方式ID
	 */
	private String paymentWayId;

	public String getPaymentWayId() {
		return this.paymentWayId;
	}

	public void setPaymentWayId(String paymentWayId) {
		this.paymentWayId = paymentWayId;
	}

	public String getCollectionIds() {
		return this.collectionIds;
	}

	public void setCollectionIds(String collectionIds) {
		this.collectionIds = collectionIds;
	}

	public String getOtherUnits() {
		return this.otherUnits;
	}

	public void setOtherUnits(String otherUnits) {
		this.otherUnits = otherUnits;
	}

	public String getCollectionDates() {
		return this.collectionDates;
	}

	public void setCollectionDates(String collectionDates) {
		this.collectionDates = collectionDates;
	}

	public String getCollectionMonth() {
		return this.collectionMonth;
	}

	public void setCollectionMonth(String collectionMonth) {
		this.collectionMonth = collectionMonth;
	}

	public String getAgents() {
		return this.agents;
	}

	public void setAgents(String agents) {
		this.agents = agents;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Double getMinMoney() {
		return this.minMoney;
	}

	public void setMinMoney(Double minMoney) {
		this.minMoney = minMoney;
	}

	public Double getMaxMoney() {
		return this.maxMoney;
	}

	public void setMaxMoney(Double maxMoney) {
		this.maxMoney = maxMoney;
	}

	public String getStartCollectionDate() {
		return this.startCollectionDate;
	}

	public void setStartCollectionDate(String startCollectionDate) {
		this.startCollectionDate = startCollectionDate;
	}

	public String getEndCollectionDate() {
		return this.endCollectionDate;
	}

	public void setEndCollectionDate(String endCollectionDate) {
		this.endCollectionDate = endCollectionDate;
	}
}
