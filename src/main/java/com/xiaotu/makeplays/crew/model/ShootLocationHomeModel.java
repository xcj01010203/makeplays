package com.xiaotu.makeplays.crew.model;

import java.util.List;


public class ShootLocationHomeModel implements java.io.Serializable {

	/**
	 * lma
	 */
	private static final long serialVersionUID = -686680934514997966L;
	
	private List<ShootLocationtBaseModel> locationBaseList;//次场景列表
	private double crewByHomeView;//按场统计拍摄地点的总戏量
	private double crewByHomePage;//按页统计地点的总戏量
	private ShootLocationtBaseModel homeView;//主场景实体
	
	public List<ShootLocationtBaseModel> getLocationBaseList() {
		return locationBaseList;
	}
	public void setLocationBaseList(List<ShootLocationtBaseModel> locationBaseList) {
		this.locationBaseList = locationBaseList;
	}
	public double getCrewByHomeView() {
		return crewByHomeView;
	}
	public void setCrewByHomeView(double crewByHomeView) {
		this.crewByHomeView = crewByHomeView;
	}
	public double getCrewByHomePage() {
		return crewByHomePage;
	}
	public void setCrewByHomePage(double crewByHomePage) {
		this.crewByHomePage = crewByHomePage;
	}
	public ShootLocationtBaseModel getHomeView() {
		return homeView;
	}
	public void setHomeView(ShootLocationtBaseModel homeView) {
		this.homeView = homeView;
	}

	
	
	
}