package com.xiaotu.makeplays.finance.model.constants;

/**
 * 借款人类型
 * @author xuchangjian 2016-8-2下午2:28:36
 */
public enum LoanerType {
	
	/**
	 * 未分类
	 */
	NoType(0),

	/**
	 * 演员
	 */
	Actor(1),
	
	/**
	 * 职员
	 */
	Worker(2),
	
	/**
	 * 第三方
	 */
	Third(3);
	
	private int value;
	
	private LoanerType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static LoanerType valueOf(int value) {
		for (LoanerType item : LoanerType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型LoanerType不支持整形值：" + value);
	}
	
	public static LoanerType nameOf(String name) {
		for (LoanerType item : LoanerType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型LoanerType不支持字面值：" + name);
	}
	
}
