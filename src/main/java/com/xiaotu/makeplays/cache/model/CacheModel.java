package com.xiaotu.makeplays.cache.model;

/**
 * @类名：CacheModel.java
 * @作者：李晓平
 * @时间：2017年6月27日 下午2:06:21
 * @描述：信息记录表
 */
public class CacheModel {

	public static final String TABLE_NAME = "tab_cache_info";
	
	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 类型，参考CacheType
	 */
	private Integer type;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 具体内容
	 */
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
