package com.xiaotu.makeplays.community.model.constants;

/**
 * 组训状态枚举类
 * @author wanrenyi 2016年9月1日下午4:15:29
 */
public enum TeamStatus {

	/**
	 * 当前组训可用
	 */
	TeamAvailable(1),
	
	/**
	 * 当前组训不可用
	 */
	TeamDisavailable(2);
	
	
	private int value;
	
	private TeamStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static TeamStatus valueOf(int value) {
		for (TeamStatus item : TeamStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型TeamStatus不支持整形值：" + value);
	}
	
	public static TeamStatus nameOf(String name) {
		for (TeamStatus item : TeamStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型TeamStatus不支持字面值：" + name);
	}
	
}
