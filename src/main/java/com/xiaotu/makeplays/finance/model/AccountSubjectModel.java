package com.xiaotu.makeplays.finance.model;

/**
 * 会计科目
 * @author xuchangjian 2016-6-22上午10:39:42
 */
public class AccountSubjectModel {

	public static final String TABLE_NAME = "tab_account_subject";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 会计科目名称
	 */
	private String name;
	
	/**
	 * 会计科目代码
	 */
	private String code;
	
	/**
	 * 排列顺序
	 */
	private int sequence;

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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
}
