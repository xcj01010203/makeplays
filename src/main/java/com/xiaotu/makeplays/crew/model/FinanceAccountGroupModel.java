package com.xiaotu.makeplays.crew.model;

import java.util.List;

/**
 * 财务科目预算分组信息表
 * @author lma
 *
 */
public class FinanceAccountGroupModel {

	public static final String TABLE_NAME="tab_finance_account_group";
	
	private String groupId;
	private String groupName;
	private String crewId;
	//lma添加字段
	private List<String> subjectIdList;
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public List<String> getSubjectIdList() {
		return subjectIdList;
	}
	public void setSubjectIdList(List<String> subjectIdList) {
		this.subjectIdList = subjectIdList;
	}
	
}
