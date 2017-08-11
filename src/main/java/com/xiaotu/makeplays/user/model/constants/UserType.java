package com.xiaotu.makeplays.user.model.constants;

/**
 * 用户类型
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum UserType {
	
	/**
	 * 剧组成员
	 */
	CrewUser(0),
	
	/**
	 * 系统管理员
	 */
	Admin(1),
	
	/**
	 * 客服
	 */
	CustomerService(2);
	
	private int value;
	
	private UserType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static UserType valueOf(int value) { 
		for (UserType item : UserType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型UserType不支持整形值：" + value);
	}
	
	public static UserType nameOf(String name) {
		for (UserType item : UserType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型UserType不支持字面值：" + name);
	}
}
