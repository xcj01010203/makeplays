package com.xiaotu.makeplays.approval.controller.filter;

/**
 * 单据查询条件
 * @author xuchangjian 2017-5-15上午11:13:28
 */
public class ReceiptInfoFilter {

	/**
	 * 单据列表类型，1-我的申请  2-我已审批  3-待我审批
	 */
	private Integer listType;
	
	/**
	 * 单据类型，1-借款  2-报销  3-预算
	 */
	private Integer receiptType;
	
	/**
	 * 单据编号（精确搜索）
	 */
	private String receiptNo;
	
	/**
	 * 申请人（模糊搜索）
	 */
	private String applyerName;
	
	/**
	 * 最大金额
	 */
	private Double maxMoney;
	
	/**
	 * 最小金额
	 */
	private Double minMoney;
	
	/**
	 * 开始日期
	 */
	private String startDate;
	
	/**
	 * 结束日期
	 */
	private String endDate;
	
	/**
	 * 单据说明（模糊搜索）
	 */
	private String description;

	public Integer getListType() {
		return this.listType;
	}

	public void setListType(Integer listType) {
		this.listType = listType;
	}

	public Integer getReceiptType() {
		return this.receiptType;
	}

	public void setReceiptType(Integer receiptType) {
		this.receiptType = receiptType;
	}

	public String getReceiptNo() {
		return this.receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getApplyerName() {
		return this.applyerName;
	}

	public void setApplyerName(String applyerName) {
		this.applyerName = applyerName;
	}

	public Double getMaxMoney() {
		return this.maxMoney;
	}

	public void setMaxMoney(Double maxMoney) {
		this.maxMoney = maxMoney;
	}

	public Double getMinMoney() {
		return this.minMoney;
	}

	public void setMinMoney(Double minMoney) {
		this.minMoney = minMoney;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
