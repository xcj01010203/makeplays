package com.xiaotu.makeplays.inhotelcost.controller.dto;

import java.util.List;

/**
 * 住宿费用中日期dto
 * @author xuchangjian 2017-2-18上午9:32:20
 */
public class InhotelCostDateDto {

	/**
	 * 入住日期
	 */
	private String checkinDate;
	
	/**
	 * 总费用
	 */
	private Double totalCost;
	
	/**
	 * 宾馆列表
	 */
	private List<InhotelCostDto> inhotelCostInfoList;

	public String getCheckinDate() {
		return this.checkinDate;
	}

	public void setCheckinDate(String checkinDate) {
		this.checkinDate = checkinDate;
	}

	public Double getTotalCost() {
		return this.totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public List<InhotelCostDto> getInhotelCostInfoList() {
		return this.inhotelCostInfoList;
	}

	public void setInhotelCostInfoList(List<InhotelCostDto> inhotelCostInfoList) {
		this.inhotelCostInfoList = inhotelCostInfoList;
	}
}
