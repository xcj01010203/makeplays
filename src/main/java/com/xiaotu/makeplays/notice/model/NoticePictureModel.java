package com.xiaotu.makeplays.notice.model;

import java.util.Date;

/**
 * 通告单预览图片信息
 * @author xuchangjian 2015-11-16下午4:08:31
 */
public class NoticePictureModel {

	public static final String TABLE_NAME = "tab_notice_picture";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 通告单版本,此处版本是根据tab_notice_time表中updateTime字段自动生成的一个版本号,格式yyyyMMddHHmmss
	 */
	private String noticeVersion;
	
	/**
	 * 图片名称
	 */
	private String name;
	
	/**
	 * 大图片存储地址
	 */
	private String bigPicurl;
	
	/**
	 * 小图片存储地址
	 */
	private String smallPicurl;
	
	/**
	 * 上传日期
	 */
	private Date uploadTime;

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

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getNoticeVersion() {
		return this.noticeVersion;
	}

	public void setNoticeVersion(String noticeVersion) {
		this.noticeVersion = noticeVersion;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBigPicurl() {
		return this.bigPicurl;
	}

	public void setBigPicurl(String bigPicurl) {
		this.bigPicurl = bigPicurl;
	}

	public String getSmallPicurl() {
		return this.smallPicurl;
	}

	public void setSmallPicurl(String smallPicurl) {
		this.smallPicurl = smallPicurl;
	}

	public Date getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
}
