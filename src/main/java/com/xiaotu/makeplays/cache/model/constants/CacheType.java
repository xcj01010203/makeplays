package com.xiaotu.makeplays.cache.model.constants;

public enum CacheType {

	/**
	 * 场景表隐藏列
	 */
	ViewHideColumn(1),
	
	/**
	 * 计划关注项
	 */
	PlanAttention(2);
	
	
	private int value;
	
	private CacheType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static CacheType valueOf(int value) {
		for (CacheType item : CacheType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CacheType不支持整形值：" + value);
	}
	
	public static CacheType nameOf(String name) {
		for (CacheType item : CacheType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CacheType不支持字面值：" + name);
	}
}
