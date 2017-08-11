package com.xiaotu.makeplays.user.model.constants;

/**
 * 人物性别
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum Sex {
	
	/**
	 * 女
	 */
	Girl(0,"女"),
	
	/**
	 * 男
	 */
	Boy(1,"男");
	
	private int value;
	private String name;
	
	private Sex(int value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	public int getValue() {
		return this.value;
	}
	
	public static Sex valueOf(int value) { 
		for (Sex item : Sex.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型Sex不支持整形值：" + value);
	}
	
	public static Sex nameOf(String name) {
		for (Sex item : Sex.values()) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型Sex不支持字面值：" + name);
	}
}
