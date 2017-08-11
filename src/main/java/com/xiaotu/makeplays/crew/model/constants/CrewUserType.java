package com.xiaotu.makeplays.crew.model.constants;

/**
 * 用户在剧组中的用户类型
 * @author xuchangjian 2016-5-19下午6:11:43
 */
public enum CrewUserType {
	/**
	 * 普通用户
	 */
	NormalUser(0),
	
	/**
	 * 剧组管理员
	 */
	Manager(1),
	
	/**
	 * 客户服务
	 */
	CustomerService(2);
	
	private int value;
	
	private CrewUserType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static CrewUserType valueOf(int value) { 
		for (CrewUserType item : CrewUserType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewUserMapType不支持整形值：" + value);
	}
	
	public static CrewUserType nameOf(String name) {
		for (CrewUserType item : CrewUserType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewUserMapType不支持字面值：" + name);
	}
}
