package com.xiaotu.makeplays.inhotelcost.model;

import java.util.Date;

/**
 * 入住费用
 * @author xuchangjian 2017-2-17上午10:56:22
 */
public class InhotelCostModel {
	
	public static final String TABLE_NAME = "tab_inhotelcost_temp";
	
	private String crewId;

	/**
	 * 入住日期
	 */
	private Date showDate;
	
	/**
	 * 宾馆名称
	 */
	private String hotelName;
	
	/**
	 * 房间号
	 */
	private String roomNumber;
	
	/**
	 * 房价
	 */
	private Double price;
	
	/**
	 * 入住人名称
	 */
	private String contactName;

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public Date getShowDate() {
		return this.showDate;
	}

	public void setShowDate(Date showDate) {
		this.showDate = showDate;
	}

	public String getHotelName() {
		return this.hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getRoomNumber() {
		return this.roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
}
