package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.util.List;

import com.xiaotu.makeplays.attachment.dto.AttachmentDto;

public class ClipCommentDto {

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
	 * 备注内容
	 */
	private String content;
	
	/**
	 * 移动端保存时间
	 */
	private String mobileTime;
	
	/**
	 * 服务端保存时间
	 */
	private String serverTime;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;
	
	/**
	 * 附件列表
	 */
	private List<AttachmentDto> attachInfoList;

	public List<AttachmentDto> getAttachInfoList() {
		return this.attachInfoList;
	}

	public void setAttachInfoList(List<AttachmentDto> attachInfoList) {
		this.attachInfoList = attachInfoList;
	}

	public String getAttpackId() {
		return this.attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
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

	public String getMobileTime() {
		return this.mobileTime;
	}

	public void setMobileTime(String mobileTime) {
		this.mobileTime = mobileTime;
	}

	public String getServerTime() {
		return this.serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
}
