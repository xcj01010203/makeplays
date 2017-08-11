package com.xiaotu.makeplays.hotelInfo.model;

import java.util.Date;

/**
 * 入住信息对象
 * @author wanrenyi 2017年3月14日下午3:36:48
 */
public class CheckinHotelInfoModel {

	public static final String TABLE_NAME = "tab_checkIn_hotel_info";
	
	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 入住人姓名
	 */
	private String peopleName;
	
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
	 * 入住时间
	 */
	private Date checkinDate;
	
	/**
	 * 退房时间
	 */
	private Date checkoutDate;
	
	/**
	 * 入住天数
	 */
	private String inTimes;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 入住宾馆id
	 */
	private String hotelId;
	
	/**
	 * 房间类型；支持自定义房间类型
	 */
	private String roomType;

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPeopleName() {
		return peopleName;
	}

	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
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

	public Date getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(Date checkinDate) {
		this.checkinDate = checkinDate;
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	public String getInTimes() {
		return inTimes;
	}

	public void setInTimes(String inTimes) {
		this.inTimes = inTimes;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
