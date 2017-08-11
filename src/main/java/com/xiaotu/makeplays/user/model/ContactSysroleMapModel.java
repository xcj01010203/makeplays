package com.xiaotu.makeplays.user.model;

/**
 * 剧组联系表和系统角色关联表
 * @author xuchangjian 2016-9-21上午10:35:51
 */
public class ContactSysroleMapModel {
	
	public static final String TABLE_NAME = "tab_contact_sysrole_map";

	private String id;
	
	private String crewId;
	
	/**
	 * 联系人ID
	 */
	private String contactId;
	
	/**
	 * 系统角色ID
	 */
	private String sysroleId;

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

	public String getContactId() {
		return this.contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getSysroleId() {
		return this.sysroleId;
	}

	public void setSysroleId(String sysroleId) {
		this.sysroleId = sysroleId;
	}
}
