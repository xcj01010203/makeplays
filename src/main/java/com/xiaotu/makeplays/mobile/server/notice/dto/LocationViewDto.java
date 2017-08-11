package com.xiaotu.makeplays.mobile.server.notice.dto;

import java.util.List;

/**
 * 通告单中按照拍摄地分组的场景信息DTO
 * @author xuchangjian 2016-9-19上午9:40:59
 */
public class LocationViewDto {

	/**
	 * 拍摄地
	 */
	private String shootLocation;
	
	/**
	 * 转场信息
	 */
	private String convertRemark = "";
	
	/**
	 * 经度
	 */
	private String vLatitude;
	
	/**
	 * 维度
	 */
	private String vLongitude;
	
	/**
	 * 场景信息
	 */
	private List<ViewInfoDto> viewInfoList;

	public String getShootLocation() {
		return this.shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}

	public String getConvertRemark() {
		return this.convertRemark;
	}

	public void setConvertRemark(String convertRemark) {
		this.convertRemark = convertRemark;
	}

	public String getvLatitude() {
		return vLatitude;
	}

	public void setvLatitude(String vLatitude) {
		this.vLatitude = vLatitude;
	}

	public String getvLongitude() {
		return vLongitude;
	}

	public void setvLongitude(String vLongitude) {
		this.vLongitude = vLongitude;
	}

	public List<ViewInfoDto> getViewInfoList() {
		return this.viewInfoList;
	}

	public void setViewInfoList(List<ViewInfoDto> viewInfoList) {
		this.viewInfoList = viewInfoList;
	}
	
	
}
