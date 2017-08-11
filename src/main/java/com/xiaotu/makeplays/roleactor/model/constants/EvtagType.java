package com.xiaotu.makeplays.roleactor.model.constants;

/**
 * 演员评价标签类型
 * @author xuchangjian 2016-7-12下午5:57:58
 */
public enum EvtagType {

	
	/**
	 * 红标签
	 */
	RedTag(1),
	
	/**
	 * 黑标签
	 */
	BlackTag(2);
	
	private int value;
	
	private EvtagType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static EvtagType valueOf(int value) {
		for (EvtagType item : EvtagType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型EvtagType不支持整形值：" + value);
	}
	
	public static EvtagType nameOf(String name) {
		for (EvtagType item : EvtagType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型EvtagType不支持字面值：" + name);
	}
	
}
