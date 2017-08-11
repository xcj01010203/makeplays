package com.xiaotu.makeplays.attachment.dto;

/**
 * 附件信息DTO
 * @author xuchangjian 2016-3-3上午9:26:13
 */
public class AttachmentDto {

	/**
	 * 附件ID
	 */
	private String attachmentId;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;
	
	/**
	 * 附件名称
	 */
	private String name;
	
	/**
	 * 附件类型
	 */
	private int type;
	
	/**
	 * 文件后缀（带点）
	 */
	private String suffix;
	
	/**
	 * 原始附件大小，单位为字节（k）
	 */
	private long size;
	
	/**
	 * 附件长度
	 * 如果为音频，则表示声音长度（单位为秒s）,如果为视频，则表示视频长度
	 */
	private long length;
	
	/**
	 * 高清附件预览地址
	 */
	private String hdPreviewUrl;
	
	/**
	 * 标清附件预览地址
	 */
	private String sdPreviewUrl;

	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLength() {
		return this.length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getAttachmentId() {
		return this.attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttpackId() {
		return this.attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getHdPreviewUrl() {
		return this.hdPreviewUrl;
	}

	public void setHdPreviewUrl(String hdPreviewUrl) {
		this.hdPreviewUrl = hdPreviewUrl;
	}

	public String getSdPreviewUrl() {
		return this.sdPreviewUrl;
	}

	public void setSdPreviewUrl(String sdPreviewUrl) {
		this.sdPreviewUrl = sdPreviewUrl;
	}
	
}
