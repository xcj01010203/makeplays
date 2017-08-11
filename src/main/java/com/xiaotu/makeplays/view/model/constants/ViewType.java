package com.xiaotu.makeplays.view.model.constants;

/**
 * 场景类型枚举类
 * @author xuchangjian
 */
public enum ViewType {

	/**
	 * 武戏
	 */
	Wuxi(1,"武戏"),
	
	/**
	 * 特效
	 */
	TeXiao(2,"特效"),
	
	/**
	 * 武特
	 */
	WuTe(3,"武特");
	
	private int value;
	private String name;
	
	private ViewType(int value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return this.value;
	}
	public String getName() {
		return this.name;
	}
	public static ViewType valueOf(int value) {
		for (ViewType item : ViewType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ViewType不支持整形值：" + value);
	}
	
	public static ViewType nameOf(String name) {
		for (ViewType item : ViewType.values()) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ViewType不支持字面值：" + name);
	}
}
