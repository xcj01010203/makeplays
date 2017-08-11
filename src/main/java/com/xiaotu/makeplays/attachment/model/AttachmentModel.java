package com.xiaotu.makeplays.attachment.model;

import java.util.Date;

/**
 * 附件表
 * @author xuchangjian 2016-3-1上午9:42:17
 */
public class AttachmentModel {
	
	public static String TABLE_NAME = "tab_attachment_info";

	private String id;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 附件类型，详情见AttachmentType枚举类
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
	private Long length;
	
	/**
	 * 高清附件存储路径
	 */
	private String hdStorePath;
	
	/**
	 * 标清附件存储路径
	 */
	private String sdStorePath;
	
	/**
	 * 附件包ID
	 */
	private String attpackId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

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

	public Long getLength() {
		return this.length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getHdStorePath() {
		return this.hdStorePath;
	}

	public void setHdStorePath(String hdStorePath) {
		this.hdStorePath = hdStorePath;
	}

	public String getSdStorePath() {
		return this.sdStorePath;
	}

	public void setSdStorePath(String sdStorePath) {
		this.sdStorePath = sdStorePath;
	}

	public String getAttpackId() {
		return this.attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
