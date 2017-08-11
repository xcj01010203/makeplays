package com.xiaotu.makeplays.hotelInfo.model;

import java.util.Date;

/**
 * 宾馆详细信息对象
 * @author wanrenyi 2017年3月14日下午3:09:23
 */
public class HotelInfoModel {

	public static final String TABLE_NAME = "tab_hotel_info";
	
	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 宾馆名称
	 */
	private String hotelName;
	
	/**
	 * 宾馆详细地址
	 */
	private String hotelAddress;
	
	/**
	 * 经度
	 */
	private String longitude;
	
	/**
	 * 维度
	 */
	private String latitude;
	
	/**
	 * 宾馆电话
	 */
	private String hotelPhone;
	
	/**
	 * 房间数
	 */
	private Integer roomNumber;
	
	/**
	 * 宾馆联系人
	 */
	private String contactPeople;
	
	/**
	 * 联系人电话
	 */
	private String contactPhone;
	
	/**
	 * 报价说明
	 */
	private String priceRemark;
	
	/**
	 * 创建时间 格式为： "yyyy-MM-dd HH:mm:ss"
	 */
	private Date createTime;
	
	/**
	 * 剧组id
	 */
	private String crewId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getHotelAddress() {
		return hotelAddress;
	}

	public void setHotelAddress(String hotelAddress) {
		this.hotelAddress = hotelAddress;
	}

	public String getHotelPhone() {
		return hotelPhone;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setHotelPhone(String hotelPhone) {
		this.hotelPhone = hotelPhone;
	}

	public Integer getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(Integer roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getContactPeople() {
		return contactPeople;
	}

	public void setContactPeople(String contactPeople) {
		this.contactPeople = contactPeople;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getPriceRemark() {
		return priceRemark;
	}

	public void setPriceRemark(String priceRemark) {
		this.priceRemark = priceRemark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
}
