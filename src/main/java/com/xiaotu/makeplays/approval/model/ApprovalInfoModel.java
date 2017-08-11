package com.xiaotu.makeplays.approval.model;

import java.util.Date;

/**
 * 审批信息
 * @author xuchangjian 2017-5-12上午10:41:38
 */
public class ApprovalInfoModel {

	public static final String TABLE_NAME = "tab_approval_info";
	
	private String id;
	
	private String crewId;
	
	/**
	 * 单据ID
	 */
	private String receiptId;
	
	/**
	 * 审核人ID
	 */
	private String approverId;
	
	/**
	 * 序列
	 */
	private Integer sequence;
	
	/**
	 * 审批结果类型，详情见ApprovalResultType枚举类
	 */
	private Integer resultType;
	
	/**
	 * 审批意见
	 */
	private String comment;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 审核时间
	 */
	private Date approvalTime;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getReceiptId() {
		return this.receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getApproverId() {
		return this.approverId;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	public Integer getSequence() {
		return this.sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getResultType() {
		return this.resultType;
	}

	public void setResultType(Integer resultType) {
		this.resultType = resultType;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getApprovalTime() {
		return this.approvalTime;
	}

	public void setApprovalTime(Date approvalTime) {
		this.approvalTime = approvalTime;
	}
}
