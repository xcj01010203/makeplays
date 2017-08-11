package com.xiaotu.makeplays.approval.model.constants;

/**
 * 单据类型
 * @author xuchangjian 2017-5-12上午11:08:08
 */
public enum ReceiptType {

	/**
	 * 借款
	 */
	Loan(1),
	
	/**
	 * 报销
	 */
	Reimbursement(2),
	
	/**
	 * 预算
	 */
	Budget(3);
	
	private int value;
	
	private ReceiptType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ReceiptType valueOf(int value) {
		for (ReceiptType item : ReceiptType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ReceiptType不支持整形值：" + value);
	}
	
	public static ReceiptType nameOf(String name) {
		for (ReceiptType item : ReceiptType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ReceiptType不支持字面值：" + name);
	}
}
