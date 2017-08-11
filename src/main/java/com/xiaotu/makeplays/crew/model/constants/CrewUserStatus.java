package com.xiaotu.makeplays.crew.model.constants;

/**
 * 剧组用户关联关系的状态
 * @author xuchangjian 2016-5-19下午6:06:59
 */
public enum CrewUserStatus {
	/**
	 * 正常
	 */
	Normal(1),
	
	/**
	 * 审核中
	 */
	Auditing(2),
	
	/**
	 * 当前剧组
	 */
	Currenct(3),
	
	/**
	 * 冻结
	 */
	Frozen(99);
	
	private int value;
	
	private CrewUserStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static CrewUserStatus valueOf(int value) { 
		for (CrewUserStatus item : CrewUserStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewUserMapStatus不支持整形值：" + value);
	}
	
	public static CrewUserStatus nameOf(String name) {
		for (CrewUserStatus item : CrewUserStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewUserMapStatus不支持字面值：" + name);
	}
}
