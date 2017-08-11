package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 财务付款方式
 * @author xuchangjian 2016-8-17下午6:13:36
 */
public class FinancePaymentWayModel {
	
	public static final String TABLE_NAME = "tab_finance_paymentWay_info";

	private String wayId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 名称
	 */
	private String wayName;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getWayId() {
		return this.wayId;
	}

	public void setWayId(String wayId) {
		this.wayId = wayId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getWayName() {
		return this.wayName;
	}

	public void setWayName(String wayName) {
		this.wayName = wayName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
