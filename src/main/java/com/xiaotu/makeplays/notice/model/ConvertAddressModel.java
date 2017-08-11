package com.xiaotu.makeplays.notice.model;

/**
 * 转场信息表
 * @author xuchangjian
 */
public class ConvertAddressModel {
	
	public static final String TABLE_NAME = "tab_convertAddress_info";

	/**
	 * ID
	 */
	private String convertId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 转场后场次ID,以逗号隔开
	 */
	private String afterViewIds;
	
	/**
	 * 转场后拍摄地ID
	 */
	private String afterLocationId;
	
	/**
	 * 转场提示
	 */
	private String remark;

	public String getConvertId() {
		return this.convertId;
	}

	public void setConvertId(String convertId) {
		this.convertId = convertId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getAfterViewIds() {
		return this.afterViewIds;
	}

	public void setAfterViewIds(String afterViewIds) {
		this.afterViewIds = afterViewIds;
	}

	public String getAfterLocationId() {
		return this.afterLocationId;
	}

	public void setAfterLocationId(String afterLocationId) {
		this.afterLocationId = afterLocationId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
