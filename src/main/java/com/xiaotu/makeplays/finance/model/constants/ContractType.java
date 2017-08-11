package com.xiaotu.makeplays.finance.model.constants;

/**
 * 合同类型
 * @author xuchangjian 2016-8-9下午2:38:24
 */
public enum ContractType {

	/**
	 * 职员
	 */
	Worker(1),
	
	/**
	 * 演员
	 */
	Actor(2),
	
	/**
	 * 制作
	 */
	Produce(3);
	
	private int value;
	
	private ContractType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ContractType valueOf(int value) {
		for (ContractType item : ContractType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ContractType不支持整形值：" + value);
	}
	
	public static ContractType nameOf(String name) {
		for (ContractType item : ContractType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ContractType不支持字面值：" + name);
	}
	
}
