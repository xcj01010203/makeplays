package com.xiaotu.makeplays.hotelInfo.controller.filter;

import java.util.Date;

/**
 * 酒店登记信息高级查询条件
 * @author xuchangjian 2017-3-18上午10:13:12
 */
public class CheckinHotelInfoFilter {

	/**
	 * 入住人姓名
	 */
	private String peopleName;
	
	/**
	 * 入住时间
	 */
	private String checkinDate;
	
	/**
	 * 退房时间
	 */
	private String checkoutDate;
	
	/**
	 * 酒店ID
	 */
	private String hotelId;
	
	/**
	 * 房间号
	 */
	private String roomNo;
	
	/**
	 * 分机号
	 */
	private String extension;
	
	/**
	 * 房价
	 */
	private Double roomPrice;
	
	/**
	 * 入住开始时间
	 */
	private String checkInStartDate;
	
	/**
	 * 入住截止时间
	 */
	private String checkInEndDate;
	
	/**
	 * 退房开始时间
	 */
	private String checkoutStartDate;
	
	/**
	 * 退房结束时间
	 */
	private String checkOutEndDate;
	
	/**
	 * 入住开始天数
	 */
	private String startInTimes;
	
	/**
	 * 入住最大天数
	 */
	private String endInTimes;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 房间类型
	 */
	private String roomType;
	
	/**
	 * 最小房价
	 */
	private Double startRoomPrice;
	
	/**
	 * 最大放假
	 */
	private Double endRoomPrice;

	public Double getStartRoomPrice() {
		return startRoomPrice;
	}

	public void setStartRoomPrice(Double startRoomPrice) {
		this.startRoomPrice = startRoomPrice;
	}

	public Double getEndRoomPrice() {
		return endRoomPrice;
	}

	public void setEndRoomPrice(Double endRoomPrice) {
		this.endRoomPrice = endRoomPrice;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public Double getRoomPrice() {
		return roomPrice;
	}

	public void setRoomPrice(Double roomPrice) {
		this.roomPrice = roomPrice;
	}

	public String getCheckInStartDate() {
		return checkInStartDate;
	}

	public void setCheckInStartDate(String checkInStartDate) {
		this.checkInStartDate = checkInStartDate;
	}

	public String getCheckInEndDate() {
		return checkInEndDate;
	}

	public void setCheckInEndDate(String checkInEndDate) {
		this.checkInEndDate = checkInEndDate;
	}

	public String getCheckoutStartDate() {
		return checkoutStartDate;
	}

	public void setCheckoutStartDate(String checkoutStartDate) {
		this.checkoutStartDate = checkoutStartDate;
	}

	public String getCheckOutEndDate() {
		return checkOutEndDate;
	}

	public void setCheckOutEndDate(String checkOutEndDate) {
		this.checkOutEndDate = checkOutEndDate;
	}

	public String getStartInTimes() {
		return startInTimes;
	}

	public void setStartInTimes(String startInTimes) {
		this.startInTimes = startInTimes;
	}

	public String getEndInTimes() {
		return endInTimes;
	}

	public void setEndInTimes(String endInTimes) {
		this.endInTimes = endInTimes;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getHotelId() {
		return this.hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getPeopleName() {
		return this.peopleName;
	}

	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}

	public String getCheckinDate() {
		return this.checkinDate;
	}

	public void setCheckinDate(String checkinDate) {
		this.checkinDate = checkinDate;
	}

	public String getCheckoutDate() {
		return this.checkoutDate;
	}

	public void setCheckoutDate(String checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	
}
