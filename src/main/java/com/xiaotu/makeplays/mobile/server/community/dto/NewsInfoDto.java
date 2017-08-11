package com.xiaotu.makeplays.mobile.server.community.dto;

/**
 * 资讯的dto
 * @author wanrenyi 2016年9月19日下午2:32:44
 */
public class NewsInfoDto {

	/**
	 * 资讯id
	 */
	private String id;
	
	/**
	 * 资讯标题
	 */
	private String title;
	
	/**
	 * 资讯简介
	 */
	private String introduction;
	
	/**
	 * 创建时间
	 */
	private String createTime;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
