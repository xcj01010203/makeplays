package com.xiaotu.makeplays.notice.model.clip;

/**
 * 场记单中道具使用信息
 * @author xuchangjian 2015-11-9下午2:59:09
 */
public class ClipPropModel {

	public static final String TABLE_NAME = "tab_clipProp_info";
	
	/**
	 * id
	 */
	private String propId;
	
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
	 * 道具名称
	 */
	private String name;
	
	/**
	 * 道具数量
	 */
	private Integer num = 1;
	
	/**
	 * 备注
	 */
	private String comment;
	
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

	public String getPropId() {
		return this.propId;
	}

	public void setPropId(String propId) {
		this.propId = propId;
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

	public String getName() {
		return this.name;
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
}
