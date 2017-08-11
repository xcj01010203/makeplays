package com.xiaotu.makeplays.roleactor.model.constants;

/**
 * 场景角色类别枚举类
 * @author xuchangjian
 */
public enum ViewRoleType {

	/**
	 * 主要演员
	 */
	MajorActor(1),
	
	/**
	 * 特约演员
	 */
	GuestActor(2),
	
	/**
	 * 群众演员
	 */
	MassesActor(3),
	
	/**
	 * 待定演员
	 */
	ToConfirmActor(4);
	
	private int value;
	
	private ViewRoleType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ViewRoleType valueOf(int value) {
		for (ViewRoleType item : ViewRoleType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ViewRoleType不支持整形值：" + value);
	}
	
	public static ViewRoleType nameOf(String name) {
		for (ViewRoleType item : ViewRoleType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ViewRoleType不支持字面值：" + name);
	}
	
}
