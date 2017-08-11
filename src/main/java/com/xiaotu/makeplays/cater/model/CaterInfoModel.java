package com.xiaotu.makeplays.cater.model;

import java.util.Date;

/**
 * 餐饮信息对象模型
 * @author wanrenyi 2017年2月20日下午2:22:18
 */
public class CaterInfoModel {
	
	public static final String TABLE_NAME = "tab_cater_info";

	/**
	 * 餐饮id（主键）
	 */
	private String caterId;
	
	/**
	 * 就餐时间
	 */
	private Date caterDate;
	
	/**
	 * 本日预算
	 */
	private Double budget;
	
	/**
	 * 剧组id
	 */
	private String crewId;

	public String getCaterId() {
		return caterId;
	}

	public void setCaterId(String caterId) {
		this.caterId = caterId;
	}

	public Date getCaterDate() {
		return caterDate;
	}

	public void setCaterDate(Date caterDate) {
		this.caterDate = caterDate;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
}
