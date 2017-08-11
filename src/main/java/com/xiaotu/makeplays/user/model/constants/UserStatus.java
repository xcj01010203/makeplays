package com.xiaotu.makeplays.user.model.constants;

/**
 * 用户状态
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum UserStatus {
	
	/**
	 * 有效
	 */
	Valid(1),
	
	/**
	 * 无效
	 */
	Invalid(2);
	
	private int value;
	
	private UserStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static UserStatus valueOf(int value) { 
		for (UserStatus item : UserStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型UserStatus不支持整形值：" + value);
	}
	
	public static UserStatus nameOf(String name) {
		for (UserStatus item : UserStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型UserStatus不支持字面值：" + name);
	}
}
