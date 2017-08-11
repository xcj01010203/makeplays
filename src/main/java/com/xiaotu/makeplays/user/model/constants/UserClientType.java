package com.xiaotu.makeplays.user.model.constants;

/**
 * 用户客户端类型
 * @author xuchangjian 2016-5-19下午6:30:59
 */
public enum UserClientType {
	/**
	 * pc
	 */
	PC(0),
	
	/**
	 * ios
	 */
	IOS(1),
	
	/**
	 * 安卓
	 */
	Android(2),
	
	/**
	 * pad
	 */
	Ipad(3);
	
	private int value;
	
	private UserClientType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static UserClientType valueOf(int value) { 
		for (UserClientType item : UserClientType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型UserClientType不支持整形值：" + value);
	}
	
	public static UserClientType nameOf(String name) {
		for (UserClientType item : UserClientType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型UserClientType不支持字面值：" + name);
	}
}
