package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.util.List;

import com.xiaotu.makeplays.view.controller.dto.CancelViewDto;


/**
 * 场记单中场景信息Dto
 * @author xuchangjian 2015-11-9下午3:55:44
 */
public class ClipViewInfoDto {

	/**
	 * 集次
	 */
	private int seriesNo;
	
	/**
	 * 场次
	 */
	private String viewNo;
	
	/**
	 * 气氛
	 */
	private String atmosphereName;
	
	/**
	 * 内外景
	 */
	private String site;
	
	/**
	 * 销场信息
	 */
	private CancelViewDto cancelViewInfo;
	
	/**
	 * 镜次信息
	 */
	private List<ShootAuditionDto> auditionList;

	public int getSeriesNo() {
		return this.seriesNo;
	}

	public void setSeriesNo(int seriesNo) {
		this.seriesNo = seriesNo;
	}

	public String getViewNo() {
		return this.viewNo;
	}

	public void setViewNo(String viewNo) {
		this.viewNo = viewNo;
	}

	public String getAtmosphereName() {
		return this.atmosphereName;
	}

	public void setAtmosphereName(String atmosphereName) {
		this.atmosphereName = atmosphereName;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public CancelViewDto getCancelViewInfo() {
		return this.cancelViewInfo;
	}

	public void setCancelViewInfo(CancelViewDto cancelViewInfo) {
		this.cancelViewInfo = cancelViewInfo;
	}

	public List<ShootAuditionDto> getAuditionList() {
		return this.auditionList;
	}

	public void setAuditionList(List<ShootAuditionDto> auditionList) {
		this.auditionList = auditionList;
	}
}
