package com.xiaotu.makeplays.car.model;

import java.util.Date;

/**
 * @类名：CarInfoModel.java
 * @作者：李晓平
 * @时间：2016年12月19日 下午7:09:01
 * @描述：车辆信息表
 */
public class CarInfoModel {

	public static final String TABLE_NAME = "tab_car_info";
	
	/**
	 * 车辆ID
	 */
	private String carId;
	
	/**
	 * 车辆编号
	 */
	private Integer carNo;
	
	/**
	 * 司机
	 */
	private String driver;
	
	/**
	 * 电话
	 */
	private String phone;
	
	/**
	 * 车辆型号
	 */
	private String carModel;
	
	/**
	 * 车牌号
	 */
	private String carNumber;
	
	/**
	 * 状态，0：离组，1：在组
	 */
	private Integer status;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 车辆用途
	 */
	private String useFor;
	
	/**
	 * 身份证号码
	 */
	private String identityNum;
	
	/**
	 * 入组日期
	 */
	private Date enterDate;
	
	/**
	 * 部门id
	 */
	private String departments;
	
	/**
	 * 车辆排序序号
	 */
	private Integer sequence;

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getCarId() {
		return this.carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public Integer getCarNo() {
		return this.carNo;
	}

	public void setCarNo(Integer carNo) {
		this.carNo = carNo;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCarModel() {
		return this.carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getCarNumber() {
		return this.carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getUseFor() {
		return this.useFor;
	}

	public void setUseFor(String useFor) {
		this.useFor = useFor;
	}

	public String getIdentityNum() {
		return this.identityNum;
	}

	public void setIdentityNum(String identityNum) {
		this.identityNum = identityNum;
	}

	public Date getEnterDate() {
		return this.enterDate;
	}

	public void setEnterDate(Date enterDate) {
		this.enterDate = enterDate;
	}
}
