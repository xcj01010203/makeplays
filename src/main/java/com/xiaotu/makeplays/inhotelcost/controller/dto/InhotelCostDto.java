package com.xiaotu.makeplays.inhotelcost.controller.dto;

/**
 * 入住费用Dto
 * @author xuchangjian 2017-2-18上午10:01:37
 */
public class InhotelCostDto {

	/**
	 * 宾馆名称
	 */
	private String hotelName;
	
	/**
	 * 住宿人数
	 */
	private int peopleNum = 0;
	
	/**
	 * 房间数
	 */
	private int roomNum = 0;
	
	/**
	 * 平均房价
	 */
	private Double avgPrice;
	
	/**
	 * 总费用
	 */
	private Double totalPrice = 0.0;

	public String getHotelName() {
		return this.hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public int getPeopleNum() {
		return this.peopleNum;
	}

	public void setPeopleNum(int peopleNum) {
		this.peopleNum = peopleNum;
	}

	public int getRoomNum() {
		return this.roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public Double getAvgPrice() {
		return this.avgPrice;
	}

	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Double getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
}
