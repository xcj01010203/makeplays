package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 场记单重要备注信息
 * @author xuchangjian 2015-11-9下午3:02:20
 */
public class ClipCommentModel {

	public static final String TABLE_NAME = "tab_clipComment_info";
	
	/**
	 * id
	 */
	private String commentId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 操作人ID
	 */
	private String userId;
	
	/**
	 * 备注内容
	 */
	private String content;
	
	/**
	 * 移动端保存时间
	 */
	private Date mobileTime;
	
	/**
	 * 服务端保存时间
	 */
	private Date serverTime;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;

	public String getAttpackId() {
		return this.attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCommentId() {
		return this.commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
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

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getMobileTime() {
		return this.mobileTime;
	}

	public void setMobileTime(Date mobileTime) {
		this.mobileTime = mobileTime;
	}

	public Date getServerTime() {
		return this.serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}
}
