package com.xiaotu.makeplays.crew.model;

import java.util.List;


public class ShootLocationModel implements java.io.Serializable {

	/**
	 * lma
	 */
	private static final long serialVersionUID = 2788606968489570499L;

	private List<ShootLocationHomeModel> homeViewList;//主场景对象
	private double locationPageTotal=0;//小计拍摄场地的页数
	private double locationCountTotal=0;//小计拍摄场地的场数
	private String shootLocation;//拍摄场地
	private String shootLocationId;//拍摄场地ID
	
	public List<ShootLocationHomeModel> getHomeViewList() {
		return homeViewList;
	}
	public void setHomeViewList(List<ShootLocationHomeModel> homeViewList) {
		this.homeViewList = homeViewList;
	}
	public double getLocationPageTotal() {
		return locationPageTotal;
	}
	public void setLocationPageTotal(double locationPageTotal) {
		this.locationPageTotal = locationPageTotal;
	}
	public double getLocationCountTotal() {
		return locationCountTotal;
	}
	public void setLocationCountTotal(double locationCountTotal) {
		this.locationCountTotal = locationCountTotal;
	}
	public String getShootLocation() {
		return shootLocation;
	}
	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}
	public String getShootLocationId() {
		return shootLocationId;
	}
	public void setShootLocationId(String shootLocationId) {
		this.shootLocationId = shootLocationId;
	}
	
	
	
	
}