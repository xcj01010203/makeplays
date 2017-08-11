package com.xiaotu.makeplays.shoot.model;

import java.util.Date;

/**
 * 拍摄分组信息
 * @author xuchangjian
 */
public class ShootGroupModel {

	public static final String TABLE_NAME = "tab_shoot_group";
	
	/**
	 * 分组ID
	 */
	private String groupId;
	
	/**
	 * 分组名称
	 */
	private String groupName;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 剧组ID
	 */
	private String crewId;

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
}
