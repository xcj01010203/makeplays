package com.xiaotu.makeplays.view.model.constants;

/**
 * 场景临时表数据类型枚举类
 * @author xuchangjian
 */
public enum ViewTempDataType {

	/**
	 * 供用户选择“跳过”或“替换”的数据
	 */
	SkipOrReplaceData(1),
	
	/**
	 * 供用户选择“保留”或“不保留”的数据
	 */
	KeepOrNotData(2);
	
	private int value;
	
	private ViewTempDataType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ViewTempDataType valueOf(int value) {
		for (ViewTempDataType item : ViewTempDataType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SceneTempDataType不支持整形值：" + value);
	}
	
	public static ViewTempDataType nameOf(String name) {
		for (ViewTempDataType item : ViewTempDataType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SceneTempDataType不支持字面值：" + name);
	}
	
}
