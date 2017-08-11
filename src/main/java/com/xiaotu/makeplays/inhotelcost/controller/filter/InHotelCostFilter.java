package com.xiaotu.makeplays.inhotelcost.controller.filter;

/**
 * 住宿费用高级查询条件
 * @author xuchangjian 2017-2-17上午9:51:44
 */
public class InHotelCostFilter {

	/**
	 * 入住月份，格式：yyyy-MM，多个以逗号隔开
	 */
	private String showDates;
	
	/**
	 * 宾馆名称，多个以逗号隔开
	 */
	private String hotelNames;
	
	/**
	 * 入住日期的开始日期，格式yyyy-MM-dd
	 */
	private String startDate;
	
	/**
	 * 入住日期的结束日期，格式yyyy-MM-dd
	 */
	private String endDate;
	
	public String getShowDates() {
		return this.showDates;
	}

	public void setShowDates(String showDates) {
		this.showDates = showDates;
	}

	public String getHotelNames() {
		return this.hotelNames;
	}

	public void setHotelNames(String hotelNames) {
		this.hotelNames = hotelNames;
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
}
