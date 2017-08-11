package com.xiaotu.makeplays.user.model.constants;

/**
 * 入组审核状态
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum JoinCrewAuditStatus {
	
	/**
	 * 审核中
	 */
	Auditing(1),
	
	/**
	 * 同意
	 */
	Agree(2),
	
	/**
	 * 拒绝
	 */
	Reject(3);
	
	private int value;
	
	private JoinCrewAuditStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static JoinCrewAuditStatus valueOf(int value) { 
		for (JoinCrewAuditStatus item : JoinCrewAuditStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型JionCrewAuditStatus不支持整形值：" + value);
	}
	
	public static JoinCrewAuditStatus nameOf(String name) {
		for (JoinCrewAuditStatus item : JoinCrewAuditStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型JionCrewAuditStatus不支持字面值：" + name);
	}
}
