package com.xiaotu.makeplays.approval.model.constants;

/**
 * 审批结果类型
 * @author xuchangjian 2017-5-12上午11:53:33
 */
public enum ApprovalResultType {
	
	/**
	 * 发起
	 */
	Launch(0),

	/**
	 * 审核中
	 */
	Auditing(1),
	
	/**
	 * 不同意
	 */
	NotAgree(2),
	
	/**
	 * 同意
	 */
	Agree(3),
	
	/**
	 * 退回
	 */
	Return(4);
	
	private int value;
	
	private ApprovalResultType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ApprovalResultType valueOf(int value) {
		for (ApprovalResultType item : ApprovalResultType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ApprovalResultType不支持整形值：" + value);
	}
	
	public static ApprovalResultType nameOf(String name) {
		for (ApprovalResultType item : ApprovalResultType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ApprovalResultType不支持字面值：" + name);
	}
}
