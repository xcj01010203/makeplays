package com.xiaotu.makeplays.scenario.model;

/**
 * 书签
 * @author xuchangjian
 */
public class BookMarkModel {
	
	public static final String TABLE_NAME = "tab_bookmarks_info";

	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 书签类型（1：剧本书签）
	 */
	private int type;
	
	/**
	 * 书签值
	 */
	private String value;

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

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
