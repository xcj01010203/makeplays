package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.util.List;

public class ClipInfoDto {

	/**
	 * 机位名称
	 */
	private String cameraName;
	
	/**
	 * 场记单场景信息
	 */
	private List<ClipViewInfoDto> viewInfoList;

	public String getCameraName() {
		return this.cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public List<ClipViewInfoDto> getViewInfoList() {
		return this.viewInfoList;
	}

	public void setViewInfoList(List<ClipViewInfoDto> viewInfoList) {
		this.viewInfoList = viewInfoList;
	}
}
