package com.xiaotu.makeplays.hotelInfo.controller.filter;

/**
 * 住宿信息高级查询条件
 * @author xuchangjian 2017-3-17下午5:41:35
 */
public class HotelInfoFilter {

	/**
	 * 入住人员名称
	 */
	private String peopleName;
	
	/**
	 * 宾馆名称
	 */
	private String hotelNames;
	
	/**
	 * 开始入住时间
	 */
	private String startDate;
	
	/**
	 * 结束入住时间
	 */
	private String endDate;

	public String getPeopleName() {
		return this.peopleName;
	}

	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
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
