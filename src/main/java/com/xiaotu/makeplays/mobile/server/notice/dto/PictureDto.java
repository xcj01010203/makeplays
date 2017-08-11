package com.xiaotu.makeplays.mobile.server.notice.dto;

/**
 * 图片信息
 * @author xuchangjian 2015-11-17下午2:18:11
 */
public class PictureDto {

	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 上传时间
	 */
	private String uploadTime;
	
	/**
	 * 大图片访问URL
	 */
	private String bigPicurl;
	
	/**
	 * 小图片访问URL
	 */
	private String smallPicurl;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
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
}
