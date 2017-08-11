package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.util.List;

import com.xiaotu.makeplays.attachment.dto.AttachmentDto;

/**
 * 场记单特殊道具
 * @author xuchangjian 2016-3-3上午9:40:16
 */
public class ClipPropInfoDto {
	/**
	 * 通告单ID
	 */
	private String noticeId;
	/**
	 * 道具ID
	 */
	private String propId;
	
	/**
	 * 名称
	 */
	private String name;

	/**
	 * 数量
	 */
	private Integer num;
	
	/**
	 * 备注
	 */
	private String comment;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;
	
	/**
	 * 附件列表
	 */
	private List<AttachmentDto> attachInfoList;

	public String getPropId() {
		return this.propId;
	}

	public void setPropId(String propId) {
		this.propId = propId;
	}

	public String getName() {
		return this.name;
	}
	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getNum() {
		return this.num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAttpackId() {
		return this.attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public List<AttachmentDto> getAttachInfoList() {
		return this.attachInfoList;
	}

	public void setAttachInfoList(List<AttachmentDto> attachInfoList) {
		this.attachInfoList = attachInfoList;
	}
}
