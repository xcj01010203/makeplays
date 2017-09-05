package com.xiaotu.makeplays.locationsearch.model;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 堪景 详细信息
 * 
 * @author Administrator
 *
 */
public class SceneViewInfoModel {
	
	public static final  String TABLE_NAME ="tab_sceneview_info";

	private String id;//主键
	
	private String vName ;//实景名称
	
	private String vCity;//所在城市

	private String vAddress;//详细地址

	private String vLongitude;//详细地址经度

	private String vLatitude;//详细地址纬度

	private String distanceToHotel;//距离住宿地距离

	private String holePeoples;//容纳人数

	private String deviceSpace;//设备空间

	private Integer isModifyView = 1;//是否改景   0：是   1： 否

	private String modifyViewCost;//改景费用
	
	private String modifyViewTime;//改景耗时

	private Integer hasProp = 1;//是否有道具陈设

	private String propCost;//道具陈设费用

	private String propTime;//道具陈设时间
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private String enterViewDate;//进景时间
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private String leaveViewDate;//离景时间

	private String viewUseTime;//使用时间

	private String contactNo;//联系方式

	private String contactName;//联系人姓名
	
	private String contactRole;//联系人职务

	private String viewPrice;//场景价格
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private String freeStartDate;//空档期开始时间
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private String freeEndDate;//空档期结束时间
	
	private String other;//自定义字段
	
	private String remark;//备注
	
	private String crewId;//剧组id

	private Integer orderNumber = 0;//排序标号

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVName() {
		return vName;
	}

	public void setvName(String vName) {
		this.vName = vName;
	}

	public String getVCity() {
		return vCity;
	}

	public void setvCity(String vCity) {
		this.vCity = vCity;
	}

	public String getVAddress() {
		return vAddress;
	}

	public void setvAddress(String vAddress) {
		this.vAddress = vAddress;
	}

	public String getVLongitude() {
		return vLongitude;
	}

	public void setvLongitude(String vLongitude) {
		this.vLongitude = vLongitude;
	}

	public String getVLatitude() {
		return vLatitude;
	}

	public void setvLatitude(String vLatitude) {
		this.vLatitude = vLatitude;
	}

	public String getDistanceToHotel() {
		return distanceToHotel;
	}

	public void setDistanceToHotel(String distanceToHotel) {
		this.distanceToHotel = distanceToHotel;
	}

	public String getHolePeoples() {
		return holePeoples;
	}

	public void setHolePeoples(String holePeoples) {
		this.holePeoples = holePeoples;
	}

	public String getDeviceSpace() {
		return deviceSpace;
	}

	public void setDeviceSpace(String deviceSpace) {
		this.deviceSpace = deviceSpace;
	}

	public Integer getIsModifyView() {
		return isModifyView;
	}

	public void setIsModifyView(Integer isModifyView) {
		this.isModifyView = isModifyView;
	}

	public String getModifyViewCost() {
		return modifyViewCost;
	}

	public void setModifyViewCost(String modifyViewCost) {
		this.modifyViewCost = modifyViewCost;
	}

	public String getModifyViewTime() {
		return modifyViewTime;
	}

	public void setModifyViewTime(String modifyViewTime) {
		this.modifyViewTime = modifyViewTime;
	}

	public Integer getHasProp() {
		return hasProp;
	}

	public void setHasProp(Integer hasProp) {
		this.hasProp = hasProp;
	}

	public String getPropCost() {
		return propCost;
	}

	public void setPropCost(String propCost) {
		this.propCost = propCost;
	}

	public String getPropTime() {
		return propTime;
	}

	public void setPropTime(String propTime) {
		this.propTime = propTime;
	}

	 

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactRole() {
		return contactRole;
	}

	public void setContactRole(String contactRole) {
		this.contactRole = contactRole;
	}

	public String getViewPrice() {
		return viewPrice;
	}

	public void setViewPrice(String viewPrice) {
		this.viewPrice = viewPrice;
	}

	 

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
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

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getEnterViewDate() {
		return enterViewDate;
	}

	public void setEnterViewDate(String enterViewDate) {
		this.enterViewDate = enterViewDate;
	}

	public String getLeaveViewDate() {
		return leaveViewDate;
	}

	public void setLeaveViewDate(String leaveViewDate) {
		this.leaveViewDate = leaveViewDate;
	}

	public String getViewUseTime() {
		return viewUseTime;
	}

	public void setViewUseTime(String viewUseTime) {
		this.viewUseTime = viewUseTime;
	}

	public String getFreeStartDate() {
		return freeStartDate;
	}

	public void setFreeStartDate(String freeStartDate) {
		this.freeStartDate = freeStartDate;
	}

	public String getFreeEndDate() {
		return freeEndDate;
	}

	public void setFreeEndDate(String freeEndDate) {
		this.freeEndDate = freeEndDate;
	}
	
	
}
