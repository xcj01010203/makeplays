package com.xiaotu.makeplays.crew.model;
/**
 * 财务科目预算分组关联信息表
 * @author lma
 *
 */
public class FinanceAccountGroupMapModel {
	public static final String TABLE_NAME="tab_finance_account_group_map";
	
	private String mapId;// '财务科目预算分组关联信息ID',
	private String accountId;//'财务预算信息ID',
	private String groupId;//'财务预算分组信息ID',
	private String  crewId;//'剧组ID',
	
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
	
	  
}
