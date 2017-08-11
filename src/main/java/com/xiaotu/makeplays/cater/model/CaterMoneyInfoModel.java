package com.xiaotu.makeplays.cater.model;

/**
 * 餐饮金额详细信息对象模型
 * @author wanrenyi 2017年2月21日上午11:28:43
 */
public class CaterMoneyInfoModel {

	public static final String TABLE_NAME = "tab_cater_money_info";
	
	/**
	 * 餐饮金额信息id
	 */
	private String caterMoneyId;
	
	/**
	 * 人数
	 */
	private Integer peopleCount;
	
	/**
	 * 份数
	 */
	private Integer caterCount;
	
	/**
	 * 餐饮类别id
	 */
	private String caterType;
	
	/**
	 * 餐饮金额
	 */
	private Double caterMoney;
	
	/**
	 * 人均
	 */
	private Double perCapita;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 餐饮id
	 */
	private String caterId;

	public String getCaterId() {
		return caterId;
	}

	public void setCaterId(String caterId) {
		this.caterId = caterId;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getCaterMoneyId() {
		return caterMoneyId;
	}

	public void setCaterMoneyId(String caterMoneyId) {
		this.caterMoneyId = caterMoneyId;
	}

	public Integer getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(Integer peopleCount) {
		this.peopleCount = peopleCount;
	}

	public Integer getCaterCount() {
		return caterCount;
	}

	public void setCaterCount(Integer caterCount) {
		this.caterCount = caterCount;
	}

	public String getCaterType() {
		return caterType;
	}

	public void setCaterType(String caterType) {
		this.caterType = caterType;
	}

	public Double getCaterMoney() {
		return caterMoney;
	}

	public void setCaterMoney(Double caterMoney) {
		this.caterMoney = caterMoney;
	}

	public Double getPerCapita() {
		return perCapita;
	}

	public void setPerCapita(Double perCapita) {
		this.perCapita = perCapita;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
