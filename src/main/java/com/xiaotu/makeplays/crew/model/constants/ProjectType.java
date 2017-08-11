package com.xiaotu.makeplays.crew.model.constants;

/**
 * @类名：ProjectType.java
 * @作者：李晓平
 * @时间：2017年2月20日 下午2:41:45
 * @描述：项目类型枚举类
 */
public enum ProjectType {
	
	/**
	 * 普通项目
	 */
	Normal(0),
	
	/**
	 * 试用项目
	 */
	Trial(1),
	
	/**
	 * 内部项目
	 */
	Internal(1);
	
	private int value;
	
	private ProjectType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ProjectType valueOf(int value) { 
		for (ProjectType item : ProjectType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ProjectType不支持整形值：" + value);
	}
	
	public static ProjectType nameOf(String name) {
		for (ProjectType item : ProjectType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ProjectType不支持字面值：" + name);
	}
}
