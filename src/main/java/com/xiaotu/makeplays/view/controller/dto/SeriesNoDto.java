package com.xiaotu.makeplays.view.controller.dto;

import java.util.List;

/**
 * 集次Dto
 * @author xuchangjian
 */
public class SeriesNoDto {

	private Integer seriesNo;	//集次号
	
	private List<ViewNoDto> viewNoDtoList;	//该集次下的场次信息

	public Integer getSeriesNo() {
		return this.seriesNo;
	}

	public void setSeriesNo(Integer seriesNo) {
		this.seriesNo = seriesNo;
	}

	public List<ViewNoDto> getViewNoDtoList() {
		return this.viewNoDtoList;
	}

	public void setViewNoDtoList(List<ViewNoDto> viewNoList) {
		this.viewNoDtoList = viewNoList;
	}
}
