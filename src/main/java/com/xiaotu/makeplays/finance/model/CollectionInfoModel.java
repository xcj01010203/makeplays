package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 收款单信息
 * @author xuchangjian 2016-8-17下午6:05:45
 */
public class CollectionInfoModel {
	
	public static final String TABLE_NAME="tab_collection_info";
	
	private String collectionId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 票据编号
	 */
	private String receiptNo;
	
	/**
	 * 收款日期
	 */
	private Date collectionDate;
	
	/**
	 * 对方单位
	 */
	private String otherUnit;
	
	/**
	 * 摘要
	 */
	private String summary;
	
	/**
	 * 金额
	 */
	private double money;
	
	/**
	 * 货币ID
	 */
	private String currencyId;  
	
	/**
	 * 付款方式ID
	 */
	private String paymentWay;
	
	/**
	 * 经办人
	 */
	private String agent;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 附加包id
	 */
	private String attpackId;

	public String getAttpackId() {
		return attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public String getCollectionId() {
		return this.collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getReceiptNo() {
		return this.receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Date getCollectionDate() {
		return this.collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getOtherUnit() {
		return this.otherUnit;
	}

	public void setOtherUnit(String otherUnit) {
		this.otherUnit = otherUnit;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getPaymentWay() {
		return this.paymentWay;
	}

	public void setPaymentWay(String paymentWay) {
		this.paymentWay = paymentWay;
	}

	public String getAgent() {
		return this.agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
