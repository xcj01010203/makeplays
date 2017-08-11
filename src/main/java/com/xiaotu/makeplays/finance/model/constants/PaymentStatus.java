package com.xiaotu.makeplays.finance.model.constants;

/**
 * 付款单状态
 * @author xuchangjian 2016-8-9下午2:39:58
 */
public enum PaymentStatus {

	/**
	 * 未结算
	 */
	NotSettle(0,"未结算"),
	
	/**
	 * 已结算
	 */
	Settled(1,"已结算");
	
	private int value;
	private String name;
	private PaymentStatus(int value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getName() {
		return name;
	}

	public static PaymentStatus valueOf(int value) {
		for (PaymentStatus item : PaymentStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型PaymentStatus不支持整形值：" + value);
	}
	
	public static PaymentStatus nameOf(String name) {
		for (PaymentStatus item : PaymentStatus.values()) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型PaymentStatus不支持字面值：" + name);
	}
}
