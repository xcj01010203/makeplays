package com.xiaotu.makeplays.finance.model.constants;

/**
 * 收款人类型 1：职员；2：演员；3：第三方；0：未分类
 * @author xuchangjian 2016-8-9下午2:33:54
 */
public enum PayeeType {

	/**
	 * 职员
	 */
	Workder(1),
	
	/**
	 * 演员
	 */
	Actor(2),
	
	/**
	 * 第三方
	 */
	Third(3);
	
	private int value;
	
	private PayeeType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static PayeeType valueOf(int value) {
		for (PayeeType item : PayeeType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型PayeeType不支持整形值：" + value);
	}
	
	public static PayeeType nameOf(String name) {
		for (PayeeType item : PayeeType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型PayeeType不支持字面值：" + name);
	}
}
