package com.xiaotu.makeplays.car.model;

import java.util.Date;

/**
 * @类名：CarWorkModel.java
 * @作者：李晓平
 * @时间：2016年12月19日 下午7:13:44
 * @描述：车辆工作表
 */
public class CarWorkModel {

	public static final String TABLE_NAME = "tab_car_work";
	
	/**
	 * 工作ID
	 */
	private String workId;
	
	/**
	 * 车辆ID
	 */
	private String carId;
	
	/**
	 * 工作日期
	 */
	private Date workDate;
	
	/**
	 * 工作开始里程表数
	 */
	private Double startMileage;
	
	/**
	 * 工作结束里程
	 */
	private Double mileage;
	
	/**
	 * 公里数
	 */
	private Double kilometers;
	
	/**
	 * 加油升数
	 */
	private Double oilLitres;
	
	/**
	 * 加油金额
	 */
	private Double oilMoney;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 备注
	 */
	private String remark;

	public Double getStartMileage() {
		return startMileage;
	}

	public void setStartMileage(Double startMileage) {
		this.startMileage = startMileage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public Double getMileage() {
		return mileage;
	}

	public void setMileage(Double mileage) {
		this.mileage = mileage;
	}

	public Double getKilometers() {
		return kilometers;
	}

	public void setKilometers(Double kilometers) {
		this.kilometers = kilometers;
	}

	public Double getOilLitres() {
		return oilLitres;
	}

	public void setOilLitres(Double oilLitres) {
		this.oilLitres = oilLitres;
	}

	public Double getOilMoney() {
		return oilMoney;
	}

	public void setOilMoney(Double oilMoney) {
		this.oilMoney = oilMoney;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
