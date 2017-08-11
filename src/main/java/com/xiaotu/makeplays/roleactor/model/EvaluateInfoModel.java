package com.xiaotu.makeplays.roleactor.model;

import java.util.Date;

/**
 * 演职员评价信息
 * @author xuchangjian 2016-7-12下午5:47:36
 */
public class EvaluateInfoModel {

	public static final String TABLE_NAME = "tab_evaluate_info";
	
	private String evaluateId;

	/**
	 * 评价人员名称
	 */
	private String fromUserName;

	/**
	 * 评价人在生产系统中的ID
	 */
	private String fromMpUserId;

	/**
	 * 被评价人员名称
	 */
	private String toUserName;

	/**
	 * 被评价人在生产系统中的ID
	 */
	private String toMpUserId;

	/**
	 * 被评价人员在剧组中的角色名称
	 */
	private String roleName;

	/**
	 * 评分
	 */
	private Integer score;

	/**
	 * 评论
	 */
	private String comment;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 评价时间
	 */
	private Date evaluateTime;

	/**
	 * 状态，详情见EvaluateStatus枚举类
	 */
	private Integer status;

	/**
	 * 对应通告单的ID
	 */
	private String noticeId;

	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getFromMpUserId() {
		return this.fromMpUserId;
	}

	public void setFromMpUserId(String fromMpUserId) {
		this.fromMpUserId = fromMpUserId;
	}

	public String getToMpUserId() {
		return this.toMpUserId;
	}

	public void setToMpUserId(String toMpUserId) {
		this.toMpUserId = toMpUserId;
	}

	public String getEvaluateId() {
		return evaluateId;
	}

	public void setEvaluateId(String evaluateId) {
		this.evaluateId = evaluateId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEvaluateTime() {
		return evaluateTime;
	}

	public void setEvaluateTime(Date evaluateTime) {
		this.evaluateTime = evaluateTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
}
