package com.xiaotu.makeplays.roleactor.controller.dto;

/**
 * 按照拍摄场景分组的场景统计信息DTO
 * @author xuchangjian 2016-7-15上午10:04:40
 */
public class ViewStatGrpByViewLocationDto {

	/**
	 * 场景ID
	 */
	private String locationId;
	
	/**
	 * 场景名称
	 */
	private String location;
	
	/**
	 * 场景类型
	 */
	private int locationType;
	
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

	public String getLocationId() {
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getLocationType() {
		return this.locationType;
	}

	public void setLocationType(int locationType) {
		this.locationType = locationType;
	}

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

	public String getAtmosphere() {
		return atmosphere;
	}

	public void setAtmosphere(String atmosphere) {
		this.atmosphere = atmosphere;
	}
}
