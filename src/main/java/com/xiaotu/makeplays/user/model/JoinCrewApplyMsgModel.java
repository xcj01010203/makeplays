package com.xiaotu.makeplays.user.model;

import java.util.Date;

/**
 * 入组申请消息
 * @author xuchangjian 2016-5-16下午3:59:37
 */
public class JoinCrewApplyMsgModel {

	public static final String TABLE_NAME = "tab_joinCrew_applyMsg";
	
	private String id;
	
	/**
	 * 申请人ID
	 */
	private String applyerId;
	
	/**
	 * 申请加入的剧组ID，多个职务用逗号隔开
	 */
	private String aimCrewId;
	
	/**
	 * 申请担当的职务，多个职务用逗号隔开
	 */
	private String aimRoleIds;
	
	/**
	 * 申请担当的职务名称
	 */
	private String aimRoleNames;
	
	/**
	 * 审核人ID
	 */
	private String dealerId;
	
	/**
	 * 状态：1表示审核中  2表示审核通过  3表示审核不通过，详细见JionCrewAuditStatus枚举类
	 */
	private int status;
	
	/**
	 * 创建时间，和申请时间是一个概念
	 */
	private Date createTime;
	
	/**
	 * 最后更新时间，和审核时间是一个概念
	 */
	private Date lastModifyTime;
	
	/**
	 * 备注
	 */
	private String remark;

	public String getAimRoleNames() {
		return this.aimRoleNames;
	}

	public void setAimRoleNames(String aimRoleNames) {
		this.aimRoleNames = aimRoleNames;
	}

	public String getDealerId() {
		return this.dealerId;
	}

	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplyerId() {
		return this.applyerId;
	}

	public void setApplyerId(String applyerId) {
		this.applyerId = applyerId;
	}

	public String getAimCrewId() {
		return this.aimCrewId;
	}

	public void setAimCrewId(String aimCrewId) {
		this.aimCrewId = aimCrewId;
	}

	public String getAimRoleIds() {
		return this.aimRoleIds;
	}

	public void setAimRoleIds(String aimRoleIds) {
		this.aimRoleIds = aimRoleIds;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastModifyTime() {
		return this.lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
