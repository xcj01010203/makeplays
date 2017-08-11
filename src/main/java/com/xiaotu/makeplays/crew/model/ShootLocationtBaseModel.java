package com.xiaotu.makeplays.crew.model;


public class ShootLocationtBaseModel implements java.io.Serializable {

	/**
	 * lma
	 */
	
	private String viewId;//场景ID
	private String shootLocationId;//拍摄场地ID
	private String shootLocation;//拍摄场地
	private String firstViewLocation;//主场景地址
	private String secondViewLocation;//次场景地址
	private double viewNo;//按场统计拍摄地点的总戏量
	private double pageCount;//按页统计地点的总戏量
	public String getViewId() {
		return viewId;
	}
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	
	public String getShootLocationId() {
		return shootLocationId;
	}
	public void setShootLocationId(String shootLocationId) {
		this.shootLocationId = shootLocationId;
	}
	public String getShootLocation() {
		return shootLocation;
	}
	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}
	public String getFirstViewLocation() {
		return firstViewLocation;
	}
	public void setFirstViewLocation(String firstViewLocation) {
		this.firstViewLocation = firstViewLocation;
	}
	public String getSecondViewLocation() {
		return secondViewLocation;
	}
	public void setSecondViewLocation(String secondViewLocation) {
		this.secondViewLocation = secondViewLocation;
	}
	public double getViewNo() {
		return viewNo;
	}
	public void setViewNo(double viewNo) {
		this.viewNo = viewNo;
	}
	public double getPageCount() {
		return pageCount;
	}
	public void setPageCount(double pageCount) {
		this.pageCount = pageCount;
	}
	
}