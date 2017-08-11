package com.xiaotu.makeplays.mobile.server.role.dto;

/**
 * 待评价人信息
 * @author xuchangjian
 */
public class ToEvaluatePersonDto {

	/**
	 * 待评价人ID
	 */
	private String toEvalatePersonId;
	
	/**
	 * 待评价人身份
	 * 目前对应主创人的职位，艺人信息中的角色
	 */
	private String identity;
	
	/**
	 * 姓名
	 */
	private String name;
	
	public String getToEvalatePersonId() {
		return this.toEvalatePersonId;
	}

	public void setToEvalatePersonId(String toEvalatePersonId) {
		this.toEvalatePersonId = toEvalatePersonId;
	}

	public String getIdentity() {
		return this.identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
