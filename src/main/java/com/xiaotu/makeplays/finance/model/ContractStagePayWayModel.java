package com.xiaotu.makeplays.finance.model;

import java.util.Date;

/**
 * 合同按阶段支付方式
 * @author xuchangjian 2016-8-13上午9:30:44
 */
public class ContractStagePayWayModel {
	
	public static final String TABLE_NAME="tab_contract_stage_pay_way";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 合同ID
	 */
	private String contractId;
	
	/**
	 * 支付金额
	 */
	private Double money;
	
	/**
	 * 支付比例：小数
	 */
	private Double rate;
	
	/**
	 * 支付提醒时间
	 */
	private Date remindTime;
	
	/**
	 * 支付阶段（按阶段支付时填写）
	 */
	private Integer stage;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

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

	public String getContractId() {
		return this.contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getRate() {
		return this.rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Date getRemindTime() {
		return this.remindTime;
	}

	public void setRemindTime(Date remindTime) {
		this.remindTime = remindTime;
	}

	public Integer getStage() {
		return this.stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
