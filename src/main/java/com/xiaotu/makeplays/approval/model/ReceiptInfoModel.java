package com.xiaotu.makeplays.approval.model;

import java.util.Date;

/**
 * 单据信息
 * @author xuchangjian 2017-5-12上午10:41:31
 */
public class ReceiptInfoModel {

	public static final String TABLE_NAME = "tab_receipt_info";
	
	private String id;
	
	private String crewId;
	
	/**
	 * 创建人ID
	 */
	private String createUserId;
	
	/**
	 * 单据类型，详情见ReceiptType枚举类
	 */
	private Integer type;
	
	/**
	 * 单据编号
	 */
	private String receiptNo;
	
	/**
	 * 单据状态，详情见ReceiptStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 金额
	 */
	private Double money;
	
	/**
	 * 币种ID
	 */
	private String currencyId;
	
	/**
	 * 说明
	 */
	private String description;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 提交时间
	 */
	private Date submitTime;
	
	/**
	 * 完结人ID
	 */
	private String doneUserId;

	public String getDoneUserId() {
		return this.doneUserId;
	}

	public void setDoneUserId(String doneUserId) {
		this.doneUserId = doneUserId;
	}

	public Date getSubmitTime() {
		return this.submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public String getAttpackId() {
		return this.attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

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

	public String getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getReceiptNo() {
		return this.receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
