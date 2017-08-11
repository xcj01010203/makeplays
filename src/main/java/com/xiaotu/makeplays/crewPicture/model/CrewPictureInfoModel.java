package com.xiaotu.makeplays.crewPicture.model;

import java.util.Date;

/**
 * 剧照信息对象
 * @author wanrenyi 2017年2月28日下午3:42:32
 */
public class CrewPictureInfoModel {
	
	public static final String TABLE_NAME = "tab_crew_picture_info";

	/**
	 * 剧照id
	 */
	private String id;
	
	/**
	 * 附件包id
	 */
	private String attpackId;
	
	/**
	 * 剧组id
	 */
	private String crewId;
	
	/**
	 * 封面照片id（及附件id）
	 */
	private String indexPictureId;
	
	/**
	 * 附件包名（及相册名）
	 */
	private String attpackName;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 分组密码
	 */
	private String picturePassword;

	/**
	 * 分组创建人id
	 */
	private String createUser;

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getPicturePassword() {
		return picturePassword;
	}

	public void setPicturePassword(String picturePassword) {
		this.picturePassword = picturePassword;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAttpackId() {
		return attpackId;
	}

	public void setAttpackId(String attpackId) {
		this.attpackId = attpackId;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getIndexPictureId() {
		return indexPictureId;
	}

	public void setIndexPictureId(String indexPictureId) {
		this.indexPictureId = indexPictureId;
	}

	public String getAttpackName() {
		return attpackName;
	}

	public void setAttpackName(String attpackName) {
		this.attpackName = attpackName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
