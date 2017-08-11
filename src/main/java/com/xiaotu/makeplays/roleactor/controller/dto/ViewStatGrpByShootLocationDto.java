package com.xiaotu.makeplays.roleactor.controller.dto;

import java.util.List;

/**
 * 按照拍摄地点分组的场景统计信息DTO
 * @author xuchangjian 2016-7-14下午5:25:14
 */
public class ViewStatGrpByShootLocationDto {

	/**
	 * 拍摄地点ID
	 */
	private String shootLocationId;
	
	/**
	 * 拍摄地点名称
	 */
	private String shootLocation;
	
	/**
	 * 场景数
	 */
	private int viewCount;
	
	/**
	 * 总页数
	 */
	private double totalPageCount;
	
	//已完成场数
	private int finishedViewCount;
	
	//已完成页数
	private double finishedPageCount;
	
	//气氛
	private String atmosphere;
	
	/**
	 * 拍摄场景统计信息列表
	 */
	private List<ViewStatGrpByViewLocationDto> viewLocationStatList;

	public int getViewCount() {
		return this.viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public double getTotalPageCount() {
		return this.totalPageCount;
	}

	public void setTotalPageCount(double totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public String getShootLocationId() {
		return this.shootLocationId;
	}

	public void setShootLocationId(String shootLocationId) {
		this.shootLocationId = shootLocationId;
	}

	public String getShootLocation() {
		return this.shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}

	public int getFinishedViewCount() {
		return finishedViewCount;
	}

	public void setFinishedViewCount(int finishedViewCount) {
		this.finishedViewCount = finishedViewCount;
	}

	public double getFinishedPageCount() {
		return finishedPageCount;
	}

	public void setFinishedPageCount(double finishedPageCount) {
		this.finishedPageCount = finishedPageCount;
	}

	public List<ViewStatGrpByViewLocationDto> getViewLocationStatList() {
		return this.viewLocationStatList;
	}

	public void setViewLocationStatList(
			List<ViewStatGrpByViewLocationDto> viewLocationStatList) {
		this.viewLocationStatList = viewLocationStatList;
	}

	public String getAtmosphere() {
		return atmosphere;
	}

	public void setAtmosphere(String atmosphere) {
		this.atmosphere = atmosphere;
	}
}
