package com.xiaotu.makeplays.cutview.model;

import java.util.Date;

/**
 * 场景剪辑对象
 * @author wanrenyi 2017年6月15日下午3:33:02
 */
public class CutViewInfoModel {
	
	public static final String TABLE_NAME = "tab_cut_view_info";

	/**
	 * 剪辑id
	 */
	private String id;
	
	/**
	 * 剪辑时长（单位：秒）
	 */
	private Integer cutLength;
	
	/**
	 * 剪辑日期（格式：yyyy-MM-dd）
	 */
	private Date cutDtae;
	
	/**
	 * 场景id
	 */
	private String viewId;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 剪辑状态（详情见CutViewStatusType 1表示已完成剪辑；2表示未完成剪辑）
	 */
	private Integer cutStatus;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 通告单id
	 */
	private String noticeId;

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getCutLength() {
		return cutLength;
	}

	public void setCutLength(Integer cutLength) {
		this.cutLength = cutLength;
	}

	public Date getCutDtae() {
		return cutDtae;
	}

	public void setCutDtae(Date cutDtae) {
		this.cutDtae = cutDtae;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Integer getCutStatus() {
		return cutStatus;
	}

	public void setCutStatus(Integer cutStatus) {
		this.cutStatus = cutStatus;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
}
