package com.xiaotu.makeplays.community.model;

import java.util.Date;

/**
 * 收藏信息表
 * @author wanrenyi 2016年9月2日下午2:44:42
 */
public class StoreInfoModel {

	public static final String TABLE_NAME = "tab_store_info";
	
	/**
	 * 收藏id
	 */
	private String storeId;
	
	/**
	 * 组训id
	 */
	private String teamId;
	
	/**
	 * 收藏人id
	 */
	private String userId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
