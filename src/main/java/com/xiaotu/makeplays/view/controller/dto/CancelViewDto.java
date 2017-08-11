package com.xiaotu.makeplays.view.controller.dto;

/**
 * 销场信息
 * @author xuchangjian 2015-11-9下午3:56:28
 */
public class CancelViewDto {

	/**
	 * 拍摄状态
	 */
	private Integer shootStatus;
	
	/**
	 * 完成日期
	 * 只有当拍摄状态为完成和加戏已完成时改字段才有效，格式：yyyy-MM-dd
	 */
	private String finishDate;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 带号
	 */
	private String tapNo;

	public String getFinishDate() {
		return this.finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public Integer getShootStatus() {
		return this.shootStatus;
	}

	public void setShootStatus(Integer shootStatus) {
		this.shootStatus = shootStatus;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTapNo() {
		return this.tapNo;
	}

	public void setTapNo(String tapNo) {
		this.tapNo = tapNo;
	}
}
