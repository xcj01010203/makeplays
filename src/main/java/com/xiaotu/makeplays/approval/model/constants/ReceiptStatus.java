package com.xiaotu.makeplays.approval.model.constants;

/**
 * 单据状态1-草稿  2-审核中  3-被拒绝  4-完结
 * @author xuchangjian 2017-5-12上午11:51:28
 */
public enum ReceiptStatus {

	/**
	 * 草稿
	 */
	Draft(1),
	
	/**
	 * 审核中
	 */
	Auditing(2),
	
	/**
	 * 被拒绝
	 */
	Rejected(3),
	
	/**
	 * 完结
	 */
	Done(4),
	
	/**
	 * 已删除
	 */
	Deleted(5);
	
	private int value;
	
	private ReceiptStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ReceiptStatus valueOf(int value) {
		for (ReceiptStatus item : ReceiptStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ReceiptStatus不支持整形值：" + value);
	}
	
	public static ReceiptStatus nameOf(String name) {
		for (ReceiptStatus item : ReceiptStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ReceiptStatus不支持字面值：" + name);
	}
}
